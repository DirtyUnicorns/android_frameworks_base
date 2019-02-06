package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.os.Handler;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.ArrayList;
import java.util.List;

public abstract class Action {
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects = new ArrayList();
    private final Handler mHandler;
    private Listener mListener;

    public interface Listener {
        void onActionAvailabilityChanged(Action action);
    }

    public Action(Context context, List<FeedbackEffect> list) {
        this.mContext = context;
        this.mHandler = new Handler(context.getMainLooper());
        if (list != null) {
            this.mFeedbackEffects.addAll(list);
        }
    }

    public static /* synthetic */ void lambda$notifyListener$0(Action action) {
        if (action.mListener != null) {
            action.mListener.onActionAvailabilityChanged(action);
        }
    }

    protected Context getContext() {
        return this.mContext;
    }

    public abstract boolean isAvailable();

    protected void notifyListener() {
        if (this.mListener != null) {
            this.mHandler.post(new _$$Lambda$Action$j2J8_IgWsMdJmJbAPdwLJPf2ZWA(this));
        }
        if (!isAvailable()) {
            this.mHandler.post(new _$$Lambda$Action$065n3tshnSDLPbdPQiUaqEYgAYY(this));
        }
    }

    public void onProgress(float f, int i) {
    }

    public abstract void onTrigger(DetectionProperties detectionProperties);

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    protected void triggerFeedbackEffects(DetectionProperties detectionProperties) {
        if (isAvailable()) {
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < this.mFeedbackEffects.size()) {
                    ((FeedbackEffect) this.mFeedbackEffects.get(i2)).onResolve(detectionProperties);
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    protected void updateFeedbackEffects(float f, int i) {
        int i2 = 0;
        int i3;
        if (f == 0.0f || i == 0) {
            while (true) {
                i3 = i2;
                if (i3 < this.mFeedbackEffects.size()) {
                    ((FeedbackEffect) this.mFeedbackEffects.get(i3)).onRelease();
                    i2 = i3 + 1;
                } else {
                    return;
                }
            }
        } else if (isAvailable()) {
            while (true) {
                i3 = i2;
                if (i3 < this.mFeedbackEffects.size()) {
                    ((FeedbackEffect) this.mFeedbackEffects.get(i3)).onProgress(f, i);
                    i2 = i3 + 1;
                } else {
                    return;
                }
            }
        }
    }
}
