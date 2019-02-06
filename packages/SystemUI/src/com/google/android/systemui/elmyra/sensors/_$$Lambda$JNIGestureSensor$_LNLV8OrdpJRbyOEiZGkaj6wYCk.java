package com.google.android.systemui.elmyra.sensors;

import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration.Listener;

public final /* synthetic */ class _$$Lambda$JNIGestureSensor$_LNLV8OrdpJRbyOEiZGkaj6wYCk implements Listener {
    private final /* synthetic */ JNIGestureSensor f$0;

    public /* synthetic */ _$$Lambda$JNIGestureSensor$_LNLV8OrdpJRbyOEiZGkaj6wYCk(JNIGestureSensor jNIGestureSensor) {
        this.f$0 = jNIGestureSensor;
    }

    public final void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration) {
        this.f$0.updateConfiguration();
    }
}
