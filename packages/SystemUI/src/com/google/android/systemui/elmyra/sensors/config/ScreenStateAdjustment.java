package com.google.android.systemui.elmyra.sensors.config;

import android.content.Context;
import android.os.PowerManager;
import android.util.TypedValue;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.R;

public class ScreenStateAdjustment extends Adjustment {
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new C16141();
    private final PowerManager mPowerManager = ((PowerManager) getContext().getSystemService("power"));
    private final float mScreenOffAdjustment;

    /* renamed from: com.google.android.systemui.elmyra.sensors.config.ScreenStateAdjustment$1 */
    class C16141 extends KeyguardUpdateMonitorCallback {
        C16141() {
        }

        public void onFinishedGoingToSleep(int i) {
            ScreenStateAdjustment.this.onSensitivityChanged();
        }

        public void onStartedWakingUp() {
            ScreenStateAdjustment.this.onSensitivityChanged();
        }
    }

    public ScreenStateAdjustment(Context context) {
        super(context);
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(R.dimen.elmyra_screen_off_adjustment, typedValue, true);
        this.mScreenOffAdjustment = typedValue.getFloat();
        KeyguardUpdateMonitor.getInstance(getContext()).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }

    public float adjustSensitivity(float f) {
        return !this.mPowerManager.isInteractive() ? f + this.mScreenOffAdjustment : f;
    }
}
