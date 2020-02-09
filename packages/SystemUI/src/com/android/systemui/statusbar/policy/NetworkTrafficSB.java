package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;

import com.android.internal.util.du.Utils;

import com.android.systemui.Dependency;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver;

public class NetworkTrafficSB extends NetworkTraffic implements DarkReceiver {
    /*
     *  @hide
     */
    public NetworkTrafficSB(Context context) {
        this(context, null);
    }

    /*
     *  @hide
     */
    public NetworkTrafficSB(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /*
     *  @hide
     */
    public NetworkTrafficSB(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(DarkIconDispatcher.class).addDarkReceiver(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(DarkIconDispatcher.class).removeDarkReceiver(this);
    }

    @Override
    protected void setMode() {
        super.setMode();
        mIsEnabled = mIsEnabled && !Utils.hasNotch(mContext);
    }

    @Override
    protected void setSpacingAndFonts() {
        setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        setLineSpacing(0.75f, 0.75f);
    }

    @Override
    protected RelativeSizeSpan getSpeedRelativeSizeSpan() {
        return new RelativeSizeSpan(0.75f);
    }

    @Override
    protected RelativeSizeSpan getUnitRelativeSizeSpan() {
        return new RelativeSizeSpan(0.7f);
    }

    @Override
    public void onDarkChanged(Rect area, float darkIntensity, int tint) {
        if (!mIsEnabled) return;
        mTintColor = DarkIconDispatcher.getTint(area, this, tint);
        setTextColor(mTintColor);
        updateTrafficDrawable();
    }
}
