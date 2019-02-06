package com.google.android.systemui.elmyra.sensors;

import java.util.Random;

public interface GestureSensor extends Sensor {

    public interface Listener {
        void onGestureDetected(GestureSensor gestureSensor, DetectionProperties detectionProperties);

        void onGestureProgress(GestureSensor gestureSensor, float f, int i);
    }

    public static class DetectionProperties {
        final long mActionId = new Random().nextLong();
        final boolean mHapticConsumed;
        final boolean mHostSuspended;

        public DetectionProperties(boolean z, boolean z2) {
            this.mHapticConsumed = z;
            this.mHostSuspended = z2;
        }

        public long getActionId() {
            return this.mActionId;
        }

        public boolean isHapticConsumed() {
            return this.mHapticConsumed;
        }

        public boolean isHostSuspended() {
            return this.mHostSuspended;
        }
    }

    void setGestureListener(Listener listener);
}
