/*
 * Copyright (C) 2013 Slimroms
 * Copyright (C) 2018 The Dirty Unicorns Project
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

    private int mRebootToRecovery = 0;
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
        switch (mRebootToRecovery) {
            default:
                mRebootToRecovery = 0; // Reboot
                break;
            case 0:
                mRebootToRecovery = 1; // Recovery
                break;
            case 1:
                mRebootToRecovery = 2; // Bootloader
                break;
            case 2:
                mRebootToRecovery = 3; // Power off
                break;
        }
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
                        RebootToRecovery();
                    });
                    return;
                } else {
                    RebootToRecovery();
                }
            }
        }, 500);
    }

    private void RebootToRecovery() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        try {
            switch (mRebootToRecovery) {
                case 0: // Reboot
                    mBarService.reboot(false);
                    break;
                case 1: // Recovery
                    mBarService.advancedReboot(PowerManager.REBOOT_RECOVERY);
                    break;
                case 2: // Bootloader
                    mBarService.advancedReboot(PowerManager.REBOOT_BOOTLOADER);
                    break;
                case 3: // Power off
                    pm.shutdown(false, pm.SHUTDOWN_USER_REQUESTED, false);
                    break;
            }
        } catch (RemoteException e) {
        }
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
        switch (mRebootToRecovery) {
            case 0: // Reboot
                state.label = mContext.getString(R.string.quick_settings_reboot_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot);
                break;
            case 1: // Recovery
                state.label = mContext.getString(R.string.quick_settings_reboot_recovery_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot_recovery);
                break;
            case 2: // Bootloader
                state.label = mContext.getString(R.string.quick_settings_reboot_bootloader_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot_bootloader);
                break;
            case 3: // Power off
                state.label = mContext.getString(R.string.quick_settings_poweroff_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_poweroff);
                break;
        }
    }

    public void handleSetListening(boolean listening) {
        if (mKeyguardMonitor == null) {
            return;
        }
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
