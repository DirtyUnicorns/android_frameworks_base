package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class NavUndimEffect implements FeedbackEffect {
    private final StatusBar mStatusBar;

    public NavUndimEffect(Context context) {
        this.mStatusBar = (StatusBar) SysUiServiceProvider.getComponent(context, StatusBar.class);
    }

    public void onProgress(float f, int i) {
        if (this.mStatusBar != null) {
            this.mStatusBar.touchAutoDim();
        }
    }

    public void onRelease() {
        if (this.mStatusBar != null) {
            this.mStatusBar.touchAutoDim();
        }
    }

    public void onResolve(DetectionProperties detectionProperties) {
        if (this.mStatusBar != null) {
            this.mStatusBar.touchAutoDim();
        }
    }
}
