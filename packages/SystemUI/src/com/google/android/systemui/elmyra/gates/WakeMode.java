package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.provider.Settings.Secure;
import com.google.android.systemui.elmyra.UserContentObserver;

public class WakeMode extends PowerState {
    private final UserContentObserver mSettingsObserver = new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_wake_enabled"), new _$$Lambda$WakeMode$lV6uvTzrddoc5zkk3T9UO9wzYhA(this), false);
    private boolean mWakeSettingEnabled;

    public WakeMode(Context context) {
        super(context);
    }

    private boolean isWakeSettingEnabled() {
        return Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_wake_enabled", 1, -2) != 0;
    }

    protected void updateWakeSetting() {
        boolean isWakeSettingEnabled = isWakeSettingEnabled();
        if (isWakeSettingEnabled != this.mWakeSettingEnabled) {
            this.mWakeSettingEnabled = isWakeSettingEnabled;
            notifyListener();
        }
    }

    protected boolean isBlocked() {
        return this.mWakeSettingEnabled ? false : super.isBlocked();
    }

    protected void onActivate() {
        this.mWakeSettingEnabled = isWakeSettingEnabled();
        this.mSettingsObserver.activate();
    }

    protected void onDeactivate() {
        this.mSettingsObserver.deactivate();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mWakeSettingEnabled -> ");
        stringBuilder.append(this.mWakeSettingEnabled);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
