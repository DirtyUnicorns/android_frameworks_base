package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class HapticClick implements FeedbackEffect {
    private static final AudioAttributes SONIFICATION_AUDIO_ATTRIBUTES = new Builder().setContentType(4).setUsage(13).build();
    private int mLastGestureStage;
    private final VibrationEffect mProgressVibrationEffect = VibrationEffect.get(5);
    private final VibrationEffect mResolveVibrationEffect = VibrationEffect.get(0);
    private final Vibrator mVibrator;

    public HapticClick(Context context) {
        this.mVibrator = (Vibrator) context.getSystemService("vibrator");
    }

    public void onProgress(float f, int i) {
        if (!(this.mLastGestureStage == 2 || i != 2 || this.mVibrator == null)) {
            this.mVibrator.vibrate(this.mProgressVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
        }
        this.mLastGestureStage = i;
    }

    public void onRelease() {
    }

    public void onResolve(DetectionProperties detectionProperties) {
        if ((detectionProperties == null || !detectionProperties.isHapticConsumed()) && this.mVibrator != null) {
            this.mVibrator.vibrate(this.mResolveVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
        }
    }
}
