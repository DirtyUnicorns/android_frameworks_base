/*
 * Copyright (C) 2020-2021 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.systemui.statusbar.policy;

import android.annotation.Nullable;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.wallpaper.WallpaperService;
import android.text.TextUtils;
//import android.util.Log;
import android.widget.Toast;

import com.android.internal.os.BackgroundThread;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.statusbar.CommandQueue;

import static com.android.systemui.Dependency.MAIN_HANDLER_NAME;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class TaskHelper implements CommandQueue.Callbacks, KeyguardMonitor.Callback {
    private static final String TAG = TaskHelper.class.getSimpleName();
    private static final String SYSTEMUI = "com.android.systemui";
    private static final String SETTINGS = "com.android.settings";

    private static final String[] DEFAULT_HOME_CHANGE_ACTIONS = new String[] {
            PackageManagerWrapper.ACTION_PREFERRED_ACTIVITY_CHANGED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_CHANGED,
            Intent.ACTION_PACKAGE_REMOVED
    };

    @Nullable
    private ComponentName mDefaultHome;
    private final ComponentName mRecentsComponentName;
    private int mRunningTaskId;
    private ComponentName mTaskComponentName;
    private Context mContext;
    private KeyguardMonitor mKeyguardMonitor;
    private PackageManager mPm;
    private boolean mKeyguardShowing;
    private TaskHelperHandler mHandler;
    private String mForegroundAppPackageName;
    private IActivityTaskManager mActivityTaskManager;
    private final Injector mInjector;
    private static final int MSG_UPDATE_FOREGROUND_APP = 0;

    private final BroadcastReceiver mDefaultHomeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mDefaultHome = getCurrentDefaultHome();
        }
    };

    private final TaskStackChangeListener mTaskStackChangeListener = new TaskStackChangeListener() {
        @Override
        public void onTaskStackChanged() {
            mHandler.sendEmptyMessage(MSG_UPDATE_FOREGROUND_APP);
        }
    };

    private final class TaskHelperHandler extends Handler {
        public TaskHelperHandler(Looper looper) {
            super(looper, null, true /*async*/);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_FOREGROUND_APP:
                    updateForegroundApp();
                    break;
            }
        }
    }

    private void updateForegroundApp() {
        // The ActivityTaskManager's lock tends to get contended, so this is done in a background
        // thread
        mInjector.getBackgroundThreadHandler().post(new Runnable() {
            public void run() {
                try {
                    // The foreground app is the top activity of the focused tasks stack.
                    final StackInfo info = mActivityTaskManager.getFocusedStackInfo();
                    mTaskComponentName = info != null ? info.topActivity : null;
                    if (mTaskComponentName == null) {
                        return;
                    }
                    mForegroundAppPackageName = mTaskComponentName.getPackageName();
                    mRunningTaskId = info.taskIds[info.taskIds.length - 1];
                } catch (RemoteException e) {
                    // Nothing to do
                }
            }
        });
    }

    public static class Injector {
        public Handler getBackgroundThreadHandler() {
            return BackgroundThread.getHandler();
        }
    }

    @Inject
    public TaskHelper(Context context, @Named(MAIN_HANDLER_NAME) Handler handler) {
        mContext = context;
        mActivityTaskManager = ActivityTaskManager.getService();
        mInjector = new Injector();
        mHandler = new TaskHelperHandler(Looper.getMainLooper());
        IntentFilter homeFilter = new IntentFilter();
        for (String action : DEFAULT_HOME_CHANGE_ACTIONS) {
            homeFilter.addAction(action);
        }
        mDefaultHome = getCurrentDefaultHome();
        mRecentsComponentName = ComponentName.unflattenFromString(context.getString(
                com.android.internal.R.string.config_recentsComponentName));
        context.registerReceiver(mDefaultHomeBroadcastReceiver, homeFilter);
        ActivityManagerWrapper.getInstance().registerTaskStackListener(mTaskStackChangeListener);
        SysUiServiceProvider.getComponent(context, CommandQueue.class).addCallback(this);
        mKeyguardMonitor = Dependency.get(KeyguardMonitor.class);
        mKeyguardMonitor.addCallback(this);
        mPm = context.getPackageManager();
        updateForegroundApp();
    }

    @Nullable
    private ComponentName getCurrentDefaultHome() {
        List<ResolveInfo> homeActivities = new ArrayList<>();
        ComponentName defaultHome = PackageManagerWrapper.getInstance()
                .getHomeActivities(homeActivities);
        if (defaultHome != null) {
            return defaultHome;
        }

        int topPriority = Integer.MIN_VALUE;
        ComponentName topComponent = null;
        for (ResolveInfo resolveInfo : homeActivities) {
            if (resolveInfo.priority > topPriority) {
                topComponent = resolveInfo.activityInfo.getComponentName();
                topPriority = resolveInfo.priority;
            } else if (resolveInfo.priority == topPriority) {
                topComponent = null;
            }
        }
        return topComponent;
    }

    @Override
    public void killForegroundApp() {
        if (isLauncherShowing()
                || !(mContext.checkCallingOrSelfPermission(
                        android.Manifest.permission.FORCE_STOP_PACKAGES) == PackageManager.PERMISSION_GRANTED)
                || isLockTaskOn()
                || mKeyguardShowing
                || mTaskComponentName == null
                || mTaskComponentName.equals(mRecentsComponentName)
                || mForegroundAppPackageName.equals(SYSTEMUI)
                || isPackageLiveWalls(mForegroundAppPackageName)) {
            return;
        }

        boolean killed = false;
        IActivityManager iam = ActivityManagerNative.getDefault();
        try {
            iam.forceStopPackage(mForegroundAppPackageName, UserHandle.USER_CURRENT); // kill
                                                                                                // app
            iam.removeTask(mRunningTaskId); // remove app from recents
            killed = true;
        } catch (RemoteException e) {
            killed = false;
        }
        if (killed) {
            String appLabel = null;
            try {
                appLabel = mPm.getActivityInfo(mTaskComponentName, 0).applicationInfo
                        .loadLabel(mPm).toString();
            } catch (Exception e) {

            }
            if (appLabel == null || appLabel.length() == 0) {
                appLabel = mContext.getString(R.string.empty_app_killed);
            }
            String toasty = mContext.getString(R.string.task_helper_app_killed, appLabel);
            Toast.makeText(mContext, toasty, Toast.LENGTH_SHORT).show();

            // Refresh current app info just in case TaskStackChangeListener callbacks don't get called properly
            mHandler.sendEmptyMessage(MSG_UPDATE_FOREGROUND_APP);
        }
    }

    @Override
    public void onKeyguardShowingChanged() {
        mKeyguardShowing = mKeyguardMonitor.isShowing();
    }

    public String getForegroundApp() {
        if (mForegroundAppPackageName == null) return "";
        return mForegroundAppPackageName;
    }

    public boolean isLauncherShowing() {
        return mTaskComponentName.equals(mDefaultHome)
                // boot time check
                || mDefaultHome.getPackageName().equals(SETTINGS);
    }

    private boolean isPackageLiveWalls(String pkg) {
        if (pkg == null) {
            return false;
        }
        List<ResolveInfo> liveWallsList = mPm.queryIntentServices(
                new Intent(WallpaperService.SERVICE_INTERFACE),
                PackageManager.GET_META_DATA);
        if (liveWallsList == null) {
            return false;
        }
        for (ResolveInfo info : liveWallsList) {
            if (info.serviceInfo != null) {
                String packageName = info.serviceInfo.packageName;
                if (TextUtils.equals(pkg, packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isLockTaskOn() {
        try {
            return ActivityManager.getService().isInLockTaskMode();
        } catch (Exception e) {
        }
        return false;
    }
}
