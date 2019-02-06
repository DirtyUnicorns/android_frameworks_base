package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.ArrayList;
import java.util.List;

public abstract class NavigationBarEffect implements FeedbackEffect {
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects = new ArrayList();

    public NavigationBarEffect(Context context) {
        this.mContext = context;
    }

    private void refreshFeedbackEffects() {
        StatusBar statusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
        if (statusBar == null || statusBar.getNavigationBarView() == null) {
            this.mFeedbackEffects.clear();
            return;
        }
        if (!validateFeedbackEffects(this.mFeedbackEffects)) {
            this.mFeedbackEffects.clear();
        }
        NavigationBarView navigationBarView = statusBar.getNavigationBarView();
        if (navigationBarView == null) {
            this.mFeedbackEffects.clear();
        }
        if (this.mFeedbackEffects.isEmpty() && navigationBarView != null) {
            this.mFeedbackEffects.addAll(findFeedbackEffects(navigationBarView));
        }
    }

    protected abstract List<FeedbackEffect> findFeedbackEffects(NavigationBarView navigationBarView);

    protected boolean isActiveFeedbackEffect(FeedbackEffect feedbackEffect) {
        return true;
    }

    public void onProgress(float f, int i) {
        refreshFeedbackEffects();
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < this.mFeedbackEffects.size()) {
                FeedbackEffect feedbackEffect = (FeedbackEffect) this.mFeedbackEffects.get(i3);
                if (isActiveFeedbackEffect(feedbackEffect)) {
                    feedbackEffect.onProgress(f, i);
                }
                i2 = i3 + 1;
            } else {
                return;
            }
        }
    }

    public void onRelease() {
        refreshFeedbackEffects();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.mFeedbackEffects.size()) {
                ((FeedbackEffect) this.mFeedbackEffects.get(i2)).onRelease();
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    public void onResolve(DetectionProperties detectionProperties) {
        refreshFeedbackEffects();
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

    protected abstract boolean validateFeedbackEffects(List<FeedbackEffect> list);
}
