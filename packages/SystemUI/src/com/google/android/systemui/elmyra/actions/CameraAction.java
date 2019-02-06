package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.List;

public class CameraAction extends ServiceAction {
    private final String mCameraPackageName;

    public CameraAction(Context context, List<FeedbackEffect> list) {
        super(context, list);
        this.mCameraPackageName = context.getResources().getString(R.string.google_camera_app_package_name);
    }

    protected boolean checkSupportedCaller() {
        return checkSupportedCaller(this.mCameraPackageName);
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        ((StatusBar) SysUiServiceProvider.getComponent(getContext(), StatusBar.class)).cancelCurrentTouch();
        super.onTrigger(detectionProperties);
    }
}
