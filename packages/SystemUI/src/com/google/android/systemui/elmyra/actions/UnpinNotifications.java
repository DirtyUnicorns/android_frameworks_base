package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class UnpinNotifications extends Action {
    private boolean mHasPinnedHeadsUp;
    private final OnHeadsUpChangedListener mHeadsUpChangedListener = new C15931();
    private final HeadsUpManager mHeadsUpManager;
    private final UserContentObserver mSettingsObserver;
    private boolean mSilenceSettingEnabled;

    /* renamed from: com.google.android.systemui.elmyra.actions.UnpinNotifications$1 */
    class C15931 implements OnHeadsUpChangedListener {
        C15931() {
        }

        public void onHeadsUpPinnedModeChanged(boolean z) {
            if (UnpinNotifications.this.mHasPinnedHeadsUp != z) {
                UnpinNotifications.this.mHasPinnedHeadsUp = z;
                UnpinNotifications.this.notifyListener();
            }
        }
    }

    public UnpinNotifications(Context context) {
        super(context, null);
        this.mHeadsUpManager = (HeadsUpManager) SysUiServiceProvider.getComponent(context, HeadsUpManager.class);
        if (this.mHeadsUpManager != null) {
            updateHeadsUpListener();
            this.mSettingsObserver = new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new _$$Lambda$UnpinNotifications$Coju1I9MwFJHZmrlRAr_VaZtdE4(this));
            return;
        }
        this.mSettingsObserver = null;
        Log.w("Elmyra/UnpinNotifications", "No HeadsUpManager");
    }

    protected void updateHeadsUpListener() {
        boolean z = true;
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
            z = false;
        }
        if (this.mSilenceSettingEnabled != z) {
            this.mSilenceSettingEnabled = z;
            if (this.mSilenceSettingEnabled) {
                this.mHasPinnedHeadsUp = this.mHeadsUpManager.hasPinnedHeadsUp();
                this.mHeadsUpManager.addListener(this.mHeadsUpChangedListener);
            } else {
                this.mHasPinnedHeadsUp = false;
                this.mHeadsUpManager.removeListener(this.mHeadsUpChangedListener);
            }
            notifyListener();
        }
    }

    public boolean isAvailable() {
        return this.mSilenceSettingEnabled ? this.mHasPinnedHeadsUp : false;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        if (this.mHeadsUpManager != null) {
            this.mHeadsUpManager.unpinAll();
        }
    }

    public String toString() {
        return super.toString();
    }
}
