/*
 * Copyright (C) 2013 Slimroms
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

package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.tileimpl.QSTileImpl;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.policy.KeyguardMonitor;

public class RebootTile extends QSTileImpl<BooleanState> {

    private boolean mRebootToRecovery = false;
    private IStatusBarService mBarService;

    private final ActivityStarter mActivityStarter;
    private final KeyguardMonitor mKeyguardMonitor;
    private final Callback mCallback = new Callback();

    private boolean mListening;

    public RebootTile(QSHost host) {
        super(host);
        mActivityStarter = Dependency.get(ActivityStarter.class);
        mKeyguardMonitor = Dependency.get(KeyguardMonitor.class);
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    public void handleClick() {
        mRebootToRecovery = !mRebootToRecovery;
        refreshState();
    }

    @Override
    protected void handleLongClick() {
        mHost.collapsePanels();
        mBarService = IStatusBarService.Stub.asInterface(
                ServiceManager.getService(Context.STATUS_BAR_SERVICE));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (mKeyguardMonitor.isSecure() && !mKeyguardMonitor.canSkipBouncer()) {
                    mActivityStarter.postQSRunnableDismissingKeyguard(() -> {
                        MetricsLogger.action(mContext, getMetricsCategory(), !mState.value);
                        try {
                            if (mRebootToRecovery)
                                mBarService.advancedReboot(PowerManager.REBOOT_RECOVERY);
                            else
                                mBarService.reboot(false);
                        } catch (RemoteException e) {
                        }
                    });
                    return;
                }
            }
        }, 500);
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_reboot_label);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CUSTOM_QUICK_TILES;
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
       if (mRebootToRecovery) {
            state.label = mContext.getString(R.string.quick_settings_reboot_recovery_label);
            state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot_recovery);
        } else {
            state.label = mContext.getString(R.string.quick_settings_reboot_label);
            state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot);
        }
    }

    public void handleSetListening(boolean listening) {
        if (mListening == listening) return;
        mListening = listening;
        if (listening) {
            mKeyguardMonitor.addCallback(mCallback);
        } else {
            mKeyguardMonitor.removeCallback(mCallback);
        }
    }

    private final class Callback implements KeyguardMonitor.Callback {
        @Override
        public void onKeyguardShowingChanged() {
            refreshState();
        }
    };
}
