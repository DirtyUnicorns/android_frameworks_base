package com.google.android.systemui.elmyra.sensors;

import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration.Listener;

public final /* synthetic */ class _$$Lambda$CHREGestureSensor$GZmCEPt8kQ_zIdv2oTQazqe1swY implements Listener {
    private final /* synthetic */ CHREGestureSensor f$0;

    public /* synthetic */ _$$Lambda$CHREGestureSensor$GZmCEPt8kQ_zIdv2oTQazqe1swY(CHREGestureSensor cHREGestureSensor) {
        this.f$0 = cHREGestureSensor;
    }

    public final void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration) {
        this.f$0.updateSensorConfiguration();
    }
}
