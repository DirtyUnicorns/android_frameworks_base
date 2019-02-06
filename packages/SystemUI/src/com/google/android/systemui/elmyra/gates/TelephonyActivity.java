package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TelephonyActivity extends Gate {
    private boolean mIsCallBlocked;
    private final PhoneStateListener mPhoneStateListener = new C16081();
    private final TelephonyManager mTelephonyManager;

    /* renamed from: com.google.android.systemui.elmyra.gates.TelephonyActivity$1 */
    class C16081 extends PhoneStateListener {
        C16081() {
        }

        public void onCallStateChanged(int i, String str) {
            boolean access$000 = TelephonyActivity.this.isCallBlocked(i);
            if (access$000 != TelephonyActivity.this.mIsCallBlocked) {
                TelephonyActivity.this.mIsCallBlocked = access$000;
                TelephonyActivity.this.notifyListener();
            }
        }
    }

    public TelephonyActivity(Context context) {
        super(context);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
    }

    private boolean isCallBlocked(int i) {
        return i == 2;
    }

    protected boolean isBlocked() {
        return this.mIsCallBlocked;
    }

    protected void onActivate() {
        this.mIsCallBlocked = isCallBlocked(this.mTelephonyManager.getCallState());
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    }

    protected void onDeactivate() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }
}
