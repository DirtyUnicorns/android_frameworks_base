package com.google.android.systemui.elmyra.gates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import com.android.internal.annotations.GuardedBy;

public class PowerSaveState extends Gate {
    @GuardedBy("mLock")
    private boolean mBatterySaverEnabled;
    @GuardedBy("mLock")
    private boolean mIsDeviceInteractive;
    private final Object mLock = new Object();
    private final PowerManager mPowerManager;
    private final BroadcastReceiver mReceiver = new C16041();

    /* renamed from: com.google.android.systemui.elmyra.gates.PowerSaveState$1 */
    class C16041 extends BroadcastReceiver {
        C16041() {
        }

        public void onReceive(Context context, Intent intent) {
            PowerSaveState.this.refreshStatus();
            PowerSaveState.this.notifyListener();
        }
    }

    public PowerSaveState(Context context) {
        super(context);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
    }

    private void refreshStatus() {
        synchronized (this.mLock) {
            this.mBatterySaverEnabled = this.mPowerManager.getPowerSaveState(13).batterySaverEnabled;
            this.mIsDeviceInteractive = this.mPowerManager.isInteractive();
        }
    }

    private boolean shouldBlock() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mBatterySaverEnabled && !this.mIsDeviceInteractive;
        }
        return z;
    }

    protected boolean isBlocked() {
        return shouldBlock();
    }

    protected void onActivate() {
        IntentFilter intentFilter = new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        getContext().registerReceiver(this.mReceiver, intentFilter);
        refreshStatus();
    }

    protected void onDeactivate() {
        getContext().unregisterReceiver(this.mReceiver);
    }
}
