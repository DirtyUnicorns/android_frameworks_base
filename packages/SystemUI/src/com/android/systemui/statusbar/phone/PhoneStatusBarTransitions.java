/*
 * Copyright (C) 2013 The Android Open Source Project
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
 * limitations under the License.
 */

package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.view.View;

import com.android.systemui.statusbar.BarTransitions;

import com.android.systemui.R;

public final class PhoneStatusBarTransitions extends BarTransitions {
    private final PhoneStatusBarView mView;

    private View mLeftSide, mStatusIcons, mSignalCluster, mBattery, mClock, mCenterClock, mNetworkTraffic, mDuLogo, mMinitBattery;

    private Animator mCurrentAnimation;

    public PhoneStatusBarTransitions(PhoneStatusBarView view) {
        super(view, R.drawable.status_background, R.color.status_bar_background_opaque,
                R.color.status_bar_background_semi_transparent,
                R.color.status_bar_background_transparent,
                com.android.internal.R.color.battery_saver_mode_color);
        mView = view;
    }

    public void init() {
        mLeftSide = mView.findViewById(R.id.notification_icon_area);
        mStatusIcons = mView.findViewById(R.id.statusIcons);
        mSignalCluster = mView.findViewById(R.id.signal_cluster);
        mBattery = mView.findViewById(R.id.battery);
        mClock = mView.findViewById(R.id.clock);
        mCenterClock = mView.findViewById(R.id.center_clock);
        mNetworkTraffic = mView.findViewById(R.id.networkTraffic);
        mDuLogo = mView.findViewById(R.id.du_logo);
        mMinitBattery = mView.findViewById(R.id.minitBattery);
        applyModeBackground(-1, getMode(), false /*animate*/);
        applyMode(getMode(), false /*animate*/);
    }

    public ObjectAnimator animateTransitionTo(View v, float toAlpha) {
        return ObjectAnimator.ofFloat(v, "alpha", v.getAlpha(), toAlpha);
    }

    @Override
    protected void onTransition(int oldMode, int newMode, boolean animate) {
        super.onTransition(oldMode, newMode, animate);
        applyMode(newMode, animate);
    }

    private void applyMode(int mode, boolean animate) {
        if (mLeftSide == null) return; // pre-init
        float newIconAlpha = mView.getIconAlpha();
        if (mCurrentAnimation != null) {
            mCurrentAnimation.cancel();
        }
        if (animate) {
            AnimatorSet anims = new AnimatorSet();
            anims.playTogether(
                    animateTransitionTo(mSignalCluster, newIconAlpha),
                    animateTransitionTo(mNetworkTraffic, newIconAlpha),
                    animateTransitionTo(mBattery, newIconAlpha),
                    animateTransitionTo(mClock, newIconAlpha),
                    animateTransitionTo(mCenterClock, newIconAlpha),
                    animateTransitionTo(mMinitBattery, newIconAlpha)
                    );
            if (isLightsOut(mode)) {
                anims.setDuration(LIGHTS_OUT_DURATION);
            }
            anims.start();
            mCurrentAnimation = anims;
        } else {
            mSignalCluster.setAlpha(newIconAlpha);
            mNetworkTraffic.setAlpha(newIconAlpha);
            mBattery.setAlpha(newIconAlpha);
            mClock.setAlpha(newIconAlpha);
            mCenterClock.setAlpha(newIconAlpha);
            mMinitBattery.setAlpha(newIconAlpha);
        }
    }
}
