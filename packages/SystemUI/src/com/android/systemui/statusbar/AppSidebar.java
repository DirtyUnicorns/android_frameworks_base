package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.android.systemui.R;

import com.android.internal.app.IUsageStats;
import com.android.internal.os.PkgUsageStats;

import java.util.*;

public class AppSidebar extends FrameLayout {
    private static final String TAG = "AppSidebar";
    private static final boolean DEBUG_LAYOUT = false;
    private static final long AUTO_HIDE_DELAY = 3000;

    private static final int SORT_TYPE_AZ = 0;
    private static final int SORT_TYPE_ZA = 1;
    private static final int SORT_TYPE_USAGE = 2;

    private static final String ACTION_HIDE_APP_CONTAINER
            = "com.android.internal.policy.statusbar.HIDE_APP_CONTAINER";

    private static enum SIDEBAR_STATE { OPENING, OPENED, CLOSING, CLOSED };
    private SIDEBAR_STATE mState = SIDEBAR_STATE.CLOSED;

    private static final LinearLayout.LayoutParams SCROLLVIEW_LAYOUT_PARAMS = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0f    );

    private static LinearLayout.LayoutParams ITEM_LAYOUT_PARAMS = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1.0f
    );

    private int mTriggerWidth;
    private View mSelectedItem;
    private LinearLayout mAppContainer;
    private SnappingScrollView mScrollView;
    private List<ImageView> mInstalledPackages;
    private TextView mInfoBubble;
    private LayoutParams mInfoBubbleParams;
    private int mSortType = SORT_TYPE_AZ;
    private float mBarAlpha = 1f;

    private IUsageStats mUsageStatsService;
    private Context mContext;
    private SettingsObserver mSettingsObserver;

    private LaunchCountComparator mLaunchCountComparator;
    private AscendingComparator mAscendingComparator;
    private DescendingComparator mDescendingComparator;

    public AppSidebar(Context context) {
        this(context, null);
    }

    public AppSidebar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppSidebar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTriggerWidth = context.getResources().getDimensionPixelSize(R.dimen.config_app_sidebar_trigger_width);
        mContext = context;
        setDrawingCacheEnabled(false);
        mLaunchCountComparator = new LaunchCountComparator();
        mAscendingComparator = new AscendingComparator();
        mDescendingComparator = new DescendingComparator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_HIDE_APP_CONTAINER);
        getContext().registerReceiver(mBroadcastReceiver, filter);
        if (DEBUG_LAYOUT)
            setBackgroundColor(0xffff0000);
        getInstalledAppsList();
        mInfoBubble = (TextView)findViewById(R.id.info_bubble);
        mInfoBubbleParams = (FrameLayout.LayoutParams)mInfoBubble.getLayoutParams();
        mUsageStatsService = IUsageStats.Stub.asInterface(ServiceManager.getService("usagestats"));
        mSettingsObserver = new SettingsObserver(new Handler());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSettingsObserver.observe();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSettingsObserver.unobserve();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_OUTSIDE:
                if (mState == SIDEBAR_STATE.OPENED)
                    showAppContainer(false);
                break;
            case MotionEvent.ACTION_DOWN:
                if (isKeyguardEnabled())
                    return false;
                if (ev.getX() <= mTriggerWidth && mState == SIDEBAR_STATE.CLOSED) {
                    showAppContainer(true);
                    cancelAutoHideTimer();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                cancelAutoHideTimer();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                updateAutoHideTimer();
                if (mState != SIDEBAR_STATE.CLOSED)
                    mState = SIDEBAR_STATE.OPENED;
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_OUTSIDE:
                if (mState == SIDEBAR_STATE.OPENED)
                    showAppContainer(false);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                updateAutoHideTimer();
                break;
            case MotionEvent.ACTION_MOVE:
            default:
                cancelAutoHideTimer();
        }
        return mScrollView.onTouchEvent(ev);
    }

    private void sortByUsage() {
        AppInfo ai;
        for (ImageView i : mInstalledPackages) {
            ai = (AppInfo)i.getTag();
            try {
                if (ai.mPackageName != null && ai.mClassName != null)
                    ai.mStats = mUsageStatsService.getPkgUsageStats(new ComponentName(ai.mPackageName,
                            ai.mClassName));
            } catch (RemoteException e) {
                ai.mStats = null;
            }
        }
        Collections.sort(mInstalledPackages, mLaunchCountComparator);
        layoutItems();
    }

    private TranslateAnimation mSlideIn = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

    private TranslateAnimation mSlideOut = new TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);

    private void showAppContainer(boolean show) {
        mSlideIn.setDuration(300);
        mSlideIn.setInterpolator(new DecelerateInterpolator());
        mSlideIn.setFillAfter(true);
        mSlideIn.setAnimationListener(mAnimListener);
        mSlideOut.setDuration(300);
        mSlideOut.setInterpolator(new DecelerateInterpolator());
        mSlideOut.setFillAfter(true);
        mSlideOut.setAnimationListener(mAnimListener);
        mState = show ? SIDEBAR_STATE.OPENING : SIDEBAR_STATE.CLOSING;
        if (show)
            mScrollView.setVisibility(View.VISIBLE);
        else {
            cancelAutoHideTimer();
            if (mInfoBubble.getVisibility() == View.VISIBLE)
                showInfoBubble(false);
        }
        mScrollView.startAnimation(show ? mSlideIn : mSlideOut);
    }

    private void showInfoBubble(boolean show) {
        mInfoBubble.setVisibility(View.VISIBLE);
        if (show) {
            start(setVisibilityWhenDone(
                    ObjectAnimator.ofFloat(mInfoBubble, View.ALPHA, 1f)
                            .setDuration(250), mInfoBubble, View.VISIBLE));
        } else {
            start(setVisibilityWhenDone(
                    ObjectAnimator.ofFloat(mInfoBubble, View.ALPHA, 0f)
                            .setDuration(250), mInfoBubble, View.GONE));
        }
    }

    private Animation.AnimationListener mAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            animation.cancel();
            mScrollView.clearAnimation();
            switch (mState) {
                case CLOSING:
                    mState = SIDEBAR_STATE.CLOSED;
                    mScrollView.setVisibility(View.GONE);
                    break;
                case OPENING:
                    mState = SIDEBAR_STATE.OPENED;
                    mScrollView.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Animator start(Animator a) {
        a.start();
        return a;
    }

    private Animator setVisibilityWhenDone(
            final Animator a, final View v, final int vis) {
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(vis);
                a.removeAllListeners(); // oneshot
            }
        });
        return a;
    }

    private boolean isKeyguardEnabled() {
        KeyguardManager km = (KeyguardManager)mContext.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    public void updateAutoHideTimer() {
        Context ctx = getContext();
        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ACTION_HIDE_APP_CONTAINER);

        PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            am.cancel(pi);
        } catch (Exception e) {
        }
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis() + AUTO_HIDE_DELAY);
        am.set(AlarmManager.RTC, time.getTimeInMillis(), pi);
    }

    public void cancelAutoHideTimer() {
        Context ctx = getContext();
        AlarmManager am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(ACTION_HIDE_APP_CONTAINER);

        PendingIntent pi = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            am.cancel(pi);
        } catch (Exception e) {
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_HIDE_APP_CONTAINER.equals(action)) {
                showAppContainer(false);
            }
        }
    };

    private void getInstalledAppsList() {
        post(new Runnable() {
            public void run() {
                PackageManager pm = getContext().getPackageManager();
                Intent localIntent = new Intent("android.intent.action.MAIN", null);
                localIntent.addCategory("android.intent.category.LAUNCHER");
                List<ResolveInfo> apps = pm.queryIntentActivities(localIntent, 0);
                mInstalledPackages = new ArrayList<ImageView>();
                ResolveInfo ri;
                for (int i = 0; i < apps.size(); i++) {
                    ri = apps.get(i);
                    AppInfo ai = new AppInfo();
                    ai.mClassName = ri.activityInfo.name;
                    ai.mPackageName = ri.activityInfo.packageName;
                    ai.mLabel = ri.activityInfo.loadLabel(pm).toString();
                    ImageView iv = new ImageView(getContext());
                    iv.setImageDrawable(ri.activityInfo.loadIcon(pm));
                    iv.setTag(ai);
                    mInstalledPackages.add(iv);
                }
                switch (mSortType) {
                    case SORT_TYPE_AZ:
                        Collections.sort(mInstalledPackages, mAscendingComparator);
                        break;
                    case SORT_TYPE_ZA:
                        Collections.sort(mInstalledPackages, mDescendingComparator);
                        break;
                    case SORT_TYPE_USAGE:
                        Collections.sort(mInstalledPackages, mLaunchCountComparator);
                        break;
                }

                layoutItems();
            }
        });
    }

    private void layoutItems() {
        if (mScrollView != null)
            removeView(mScrollView);

        // create a linearlayout to hold our items
        if (mAppContainer == null) {
            mAppContainer = new LinearLayout(mContext);
            mAppContainer.setOrientation(LinearLayout.VERTICAL);
            mAppContainer.setGravity(Gravity.CENTER);
        }
        mAppContainer.removeAllViews();

        // set the layout height based on the item height we would like and the
        // number of items that would fit at on screen at once given the height
        // of the app sidebar
        int desiredHeight = mContext.getResources().getDimensionPixelSize(R.dimen.app_sidebar_item_height);
        int numItems = (int)Math.floor(getHeight() / desiredHeight);
        ITEM_LAYOUT_PARAMS.height = getHeight() / numItems;

        for (ImageView icon : mInstalledPackages) {
            mAppContainer.addView(icon, ITEM_LAYOUT_PARAMS);
            icon.setOnClickListener(mItemClickedListener);
            icon.setOnTouchListener(mItemTouchedListener);
            icon.setClickable(true);
        }

        // we need our horizontal scroll view to wrap the linear layout
        if (mScrollView == null) {
            mScrollView = new SnappingScrollView(mContext);
            // make the fading edge the size of a button (makes it more noticible that we can scroll
            mScrollView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
            mScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            mScrollView.setBackgroundResource(R.drawable.app_sidebar_background);
        }
        mScrollView.removeAllViews();
        mScrollView.addView(mAppContainer, SCROLLVIEW_LAYOUT_PARAMS);
        addView(mScrollView, SCROLLVIEW_LAYOUT_PARAMS);
        mScrollView.setAlpha(mBarAlpha);
        mScrollView.setVisibility(View.GONE);
    }

    private void launchApplication(AppInfo ai) {
        PackageManager pm = mContext.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(ai.mPackageName);
        mContext.startActivity(intent);
        showAppContainer(false);
    }

    private OnClickListener mItemClickedListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mState != SIDEBAR_STATE.OPENED)
                return;

            launchApplication((AppInfo)view.getTag());
        }
    };

    private OnTouchListener mItemTouchedListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (mState != SIDEBAR_STATE.OPENED)
                return false;
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    AppInfo ai = (AppInfo)view.getTag();
                    mInfoBubble.bringToFront();
                    mInfoBubble.setText(ai.mLabel);
                    mSelectedItem = view;
                    positionInfoBubble(view, mScrollView.getScrollY());
                    showInfoBubble(true);
                    break;
            }
            return false;
        }
    };

    private void positionInfoBubble(View v, int scrollOffset) {
        int marginTop = v.getTop() + v.getHeight()/2
                - mInfoBubble.getHeight()/2 - scrollOffset;
        Log.i(TAG, "positionInfoBubble: scrollOffset=" + scrollOffset + " marginTop=" + marginTop);
        mInfoBubbleParams.setMargins(mScrollView.getWidth(), marginTop, 0, 0);
        mInfoBubble.setLayoutParams(mInfoBubbleParams);
    }

    class SnappingScrollView extends ScrollView {

        private boolean mSnapTrigger = false;

        public SnappingScrollView(Context context) {
            super(context);
        }

        Runnable mSnapRunnable = new Runnable(){
            @Override
            public void run() {
                int mSelectedItem = ((getScrollY() + (ITEM_LAYOUT_PARAMS.height / 2)) / ITEM_LAYOUT_PARAMS.height);
                int scrollTo = mSelectedItem * ITEM_LAYOUT_PARAMS.height;
                smoothScrollTo(0, scrollTo);
                mSnapTrigger = false;
            }
        };

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            if (mSelectedItem != null)
                positionInfoBubble(mSelectedItem, t);
            if (Math.abs(oldt - t) <= 1 && mSnapTrigger) {
                removeCallbacks(mSnapRunnable);
                postDelayed(mSnapRunnable, 100);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                showInfoBubble(false);
                mSnapTrigger = true;
                updateAutoHideTimer();
                if (mState != SIDEBAR_STATE.OPENED)
                    return false;

                if (ev.getX() > this.getWidth()*2 && mSelectedItem != null &&
                        mInfoBubble.getVisibility() == View.VISIBLE) {
                    launchApplication((AppInfo)mSelectedItem.getTag());
                }
            }
            return super.onTouchEvent(ev);
        }
    }

    class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.APP_SIDE_BAR_ENABLED), false, this);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.APP_SIDEBAR_SORT_TYPE), false, this);
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.APP_SIDEBAR_TRANSPARENCY), false, this);
            update();
        }

        void unobserve() {
            mContext.getContentResolver().unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            update();
        }

        public void update() {
            ContentResolver resolver = mContext.getContentResolver();
            boolean enabled = Settings.System.getInt(
                    resolver, Settings.System.APP_SIDE_BAR_ENABLED, 0) == 1;
            setVisibility(enabled ? View.VISIBLE : View.GONE);

            int sortType = Settings.System.getInt(
                    resolver, Settings.System.APP_SIDEBAR_SORT_TYPE, SORT_TYPE_AZ);
            if (mInstalledPackages != null && sortType != mSortType) {
                switch (sortType) {
                    case SORT_TYPE_AZ:
                        Collections.sort(mInstalledPackages, mAscendingComparator);
                        break;
                    case SORT_TYPE_ZA:
                        Collections.sort(mInstalledPackages, mDescendingComparator);
                        break;
                }
                layoutItems();
            }
            mSortType = sortType;

            float barAlpha = (float)(100 - Settings.System.getInt(
                    resolver, Settings.System.APP_SIDEBAR_TRANSPARENCY, 0)) / 100f;
            if (barAlpha != mBarAlpha) {
                if (mScrollView != null)
                    mScrollView.setAlpha(barAlpha);
                mBarAlpha = barAlpha;
            }
        }
    }

    private class AppInfo {
        String mLabel;
        String mPackageName;
        String mClassName;
        PkgUsageStats mStats;
    }

    public static class LaunchCountComparator implements Comparator<ImageView> {
        public final int compare(ImageView a, ImageView b) {
            // return by descending order
            AppInfo ai = ((AppInfo)a.getTag());
            AppInfo bi = ((AppInfo)b.getTag());
            int aLaunchCount = ai.mStats != null ? ai.mStats.launchCount : 0;
            int bLaunchCount = bi.mStats != null ? bi.mStats.launchCount : 0;
            return bLaunchCount - aLaunchCount;
        }
    }

    public static class AscendingComparator implements Comparator<ImageView> {
        public final int compare(ImageView a, ImageView b) {
            String alabel = ((AppInfo)a.getTag()).mLabel;
            String blabel = ((AppInfo)b.getTag()).mLabel;
            return alabel.compareTo(blabel);
        }
    }

    public static class DescendingComparator implements Comparator<ImageView> {
        public final int compare(ImageView a, ImageView b) {
            String alabel = ((AppInfo)a.getTag()).mLabel;
            String blabel = ((AppInfo)b.getTag()).mLabel;
            return blabel.compareTo(alabel);
        }
    }
}
