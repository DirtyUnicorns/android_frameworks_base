package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class UserActivity implements FeedbackEffect {
    private final KeyguardMonitor mKeyguardMonitor = ((KeyguardMonitor) Dependency.get(KeyguardMonitor.class));
    private int mLastStage = 0;
    private final PowerManager mPowerManager;
    private int mTriggerCount = 0;

    public UserActivity(Context context) {
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    public void onProgress(float f, int i) {
        if (!(i == this.mLastStage || i != 2 || this.mKeyguardMonitor.isShowing() || this.mPowerManager == null)) {
            this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
            this.mTriggerCount++;
        }
        this.mLastStage = i;
    }

    public void onRelease() {
    }

    public void onResolve(DetectionProperties detectionProperties) {
        this.mTriggerCount--;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mTriggerCount -> ");
        stringBuilder.append(this.mTriggerCount);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
