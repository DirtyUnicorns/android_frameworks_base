package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

import com.android.internal.util.du.ActionUtils;

public class Flashlight extends Action {

    public Flashlight(Context context) {
        super(context, null);
    }

    public boolean isAvailable() {
        return true;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        ActionUtils.toggleCameraFlash();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mFlashlightEnabled -> ");
        stringBuilder.append(true/*this.mFlashlightEnabled*/);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
