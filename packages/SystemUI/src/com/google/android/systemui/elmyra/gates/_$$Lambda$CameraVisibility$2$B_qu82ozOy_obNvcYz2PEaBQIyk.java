package com.google.android.systemui.elmyra.gates;

public final /* synthetic */ class _$$Lambda$CameraVisibility$2$B_qu82ozOy_obNvcYz2PEaBQIyk implements Runnable {
    private final /* synthetic */ CameraVisibility f$0;

    public /* synthetic */ _$$Lambda$CameraVisibility$2$B_qu82ozOy_obNvcYz2PEaBQIyk(CameraVisibility cameraVisibility) {
        this.f$0 = cameraVisibility;
    }

    public final void run() {
        this.f$0.updateCameraIsShowing();
    }
}
