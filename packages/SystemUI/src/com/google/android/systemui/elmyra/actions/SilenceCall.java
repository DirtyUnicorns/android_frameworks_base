package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class SilenceCall extends Action {
    private boolean mIsPhoneRinging;
    private final PhoneStateListener mPhoneStateListener = new C15921();
    private final UserContentObserver mSettingsObserver;
    private boolean mSilenceSettingEnabled;
    private final TelephonyManager mTelephonyManager = ((TelephonyManager) getContext().getSystemService("phone"));

    /* renamed from: com.google.android.systemui.elmyra.actions.SilenceCall$1 */
    class C15921 extends PhoneStateListener {
        C15921() {
        }

        public void onCallStateChanged(int i, String str) {
            boolean access$000 = SilenceCall.this.isPhoneRinging(i);
            if (SilenceCall.this.mIsPhoneRinging != access$000) {
                SilenceCall.this.mIsPhoneRinging = access$000;
                SilenceCall.this.notifyListener();
            }
        }
    }

    public SilenceCall(Context context) {
        super(context, null);
        updatePhoneStateListener();
        this.mSettingsObserver = new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new _$$Lambda$SilenceCall$P91IyaoSIoRZpeDIyPp8173JrBg(this));
    }

    private boolean isPhoneRinging(int i) {
        return i == 1;
    }

    protected void updatePhoneStateListener() {
        boolean z = true;
        int i = 0;
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
            z = false;
        }
        if (z != this.mSilenceSettingEnabled) {
            this.mSilenceSettingEnabled = z;
            if (this.mSilenceSettingEnabled) {
                i = 32;
            }
            this.mTelephonyManager.listen(this.mPhoneStateListener, i);
            this.mIsPhoneRinging = isPhoneRinging(this.mTelephonyManager.getCallState());
            notifyListener();
        }
    }

    public boolean isAvailable() {
        return this.mSilenceSettingEnabled ? this.mIsPhoneRinging : false;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mTelephonyManager.silenceRinger();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mSilenceSettingEnabled -> ");
        stringBuilder.append(this.mSilenceSettingEnabled);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
