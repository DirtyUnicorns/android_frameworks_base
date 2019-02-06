package com.google.android.systemui.elmyra.sensors.config;

import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$GestureConfiguration$F1rbWa9DGNKbISCQL2RDoKSl7Sw implements Consumer {
    private final /* synthetic */ GestureConfiguration f$0;

    public /* synthetic */ _$$Lambda$GestureConfiguration$F1rbWa9DGNKbISCQL2RDoKSl7Sw(GestureConfiguration gestureConfiguration) {
        this.f$0 = gestureConfiguration;
    }

    public final void accept(Object obj) {
        ((Adjustment) obj).setCallback(this.f$0.mAdjustmentCallback);
    }
}
