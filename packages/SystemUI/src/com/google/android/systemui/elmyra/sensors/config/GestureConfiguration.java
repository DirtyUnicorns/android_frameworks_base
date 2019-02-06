package com.google.android.systemui.elmyra.sensors.config;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings.Secure;
import android.util.Range;
import com.android.systemui.R;
import com.google.android.systemui.elmyra.UserContentObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GestureConfiguration {
    private static final Range<Float> SENSITIVITY_RANGE = Range.create(Float.valueOf(0.0f), Float.valueOf(1.0f));
    protected final Consumer<Adjustment> mAdjustmentCallback = new _$$Lambda$GestureConfiguration$3mm6FunisrpGZpM7qxO1no0tVbU(this);
    private final List<Adjustment> mAdjustments;
    private final Context mContext;
    private Listener mListener;
    private int[] mLowerThreshold;
    private float mSensitivity = 0.5f;
    private final UserContentObserver mSettingsObserver;
    private int[] mSlopeSensitivity;
    private int[] mTimeWindow;
    private int[] mUpperThreshold;

    public interface Listener {
        void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration);
    }

    public GestureConfiguration(Context context, List<Adjustment> list) {
        this.mContext = context;
        this.mAdjustments = new ArrayList(list);
        this.mAdjustments.forEach(new _$$Lambda$GestureConfiguration$F1rbWa9DGNKbISCQL2RDoKSl7Sw(this));
        Resources resources = context.getResources();
        this.mSettingsObserver = new UserContentObserver(this.mContext, Secure.getUriFor("assist_gesture_sensitivity"), new _$$Lambda$GestureConfiguration$qyMZ0LytUPraF62LfdN_eAAd2vo(this));
        this.mUpperThreshold = resources.getIntArray(R.array.elmyra_upper_threshold);
        this.mSlopeSensitivity = resources.getIntArray(R.array.elmyra_slope_sensitivity);
        this.mLowerThreshold = resources.getIntArray(R.array.elmyra_lower_threshold);
        this.mTimeWindow = resources.getIntArray(R.array.elmyra_time_window);
        this.mSensitivity = getUserSensitivity();
    }

    private float calculateFraction(float f, float f2, float f3) {
        return ((f2 - f) * f3) + f;
    }

    private float calculateFraction(int[] iArr, float f) {
        return calculateFraction((float) iArr[1], (float) iArr[0], f);
    }

    private float getUserSensitivity() {
        float floatForUser = Secure.getFloatForUser(this.mContext.getContentResolver(), "assist_gesture_sensitivity", 0.5f, -2);
        return !SENSITIVITY_RANGE.contains(Float.valueOf(floatForUser)) ? 0.5f : floatForUser;
    }

    public float getLowerThreshold() {
        return calculateFraction(this.mLowerThreshold, getSensitivity());
    }

    public float getSensitivity() {
        int i = 0;
        float f = this.mSensitivity;
        while (true) {
            int i2 = i;
            if (i2 >= this.mAdjustments.size()) {
                return f;
            }
            f = ((Float) SENSITIVITY_RANGE.clamp(Float.valueOf(((Adjustment) this.mAdjustments.get(i2)).adjustSensitivity(f)))).floatValue();
            i = i2 + 1;
        }
    }

    public float getSlopeSensitivity() {
        return calculateFraction(this.mSlopeSensitivity, getSensitivity()) / 100.0f;
    }

    public int getTimeWindow() {
        return (int) calculateFraction(this.mTimeWindow, getSensitivity());
    }

    public float getUpperThreshold() {
        return calculateFraction(this.mUpperThreshold, getSensitivity());
    }

    /* renamed from: onSensitivityChanged */
    public void lambda$new$2() {
        this.mSensitivity = getUserSensitivity();
        if (this.mListener != null) {
            this.mListener.onGestureConfigurationChanged(this);
        }
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }
}
