package com.google.android.systemui.elmyra.gates;

public final /* synthetic */ class _$$Lambda$CameraVisibility$1$X_K32nTSgqALN1DA7GlsqyIM0Ns implements Runnable {
    private final /* synthetic */ CameraVisibility f$0;

    public /* synthetic */ _$$Lambda$CameraVisibility$1$X_K32nTSgqALN1DA7GlsqyIM0Ns(CameraVisibility cameraVisibility) {
        this.f$0 = cameraVisibility;
    }

    public final void run() {
        this.f$0.updateCameraIsShowing();
    }
}
