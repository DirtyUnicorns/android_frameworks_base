package com.google.android.systemui.elmyra.sensors.config;

import android.content.Context;
import java.util.function.Consumer;

public abstract class Adjustment {
    private Consumer<Adjustment> mCallback;
    private final Context mContext;

    public Adjustment(Context context) {
        this.mContext = context;
    }

    public abstract float adjustSensitivity(float f);

    protected Context getContext() {
        return this.mContext;
    }

    protected void onSensitivityChanged() {
        if (this.mCallback != null) {
            this.mCallback.accept(this);
        }
    }

    public void setCallback(Consumer<Adjustment> consumer) {
        this.mCallback = consumer;
    }
}
