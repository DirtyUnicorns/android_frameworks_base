package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.PowerManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;

public class PowerState extends Gate {
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new C16051();
    private final PowerManager mPowerManager;

    /* renamed from: com.google.android.systemui.elmyra.gates.PowerState$1 */
    class C16051 extends KeyguardUpdateMonitorCallback {
        C16051() {
        }

        public void onFinishedGoingToSleep(int i) {
            PowerState.this.notifyListener();
        }

        public void onStartedWakingUp() {
            PowerState.this.notifyListener();
        }
    }

    public PowerState(Context context) {
        super(context);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
    }

    protected boolean isBlocked() {
        return !this.mPowerManager.isInteractive();
    }

    protected void onActivate() {
        KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }

    protected void onDeactivate() {
        KeyguardUpdateMonitor.getInstance(getContext()).removeCallback(this.mKeyguardUpdateMonitorCallback);
    }
}
