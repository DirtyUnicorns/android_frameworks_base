package com.google.android.systemui.elmyra.actions;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

abstract class DeskClockAction extends Action {
    private boolean mAlertFiring;
    private final BroadcastReceiver mAlertReceiver = new C15881();
    private boolean mReceiverRegistered;
    private final UserContentObserver mSettingsObserver;

    /* renamed from: com.google.android.systemui.elmyra.actions.DeskClockAction$1 */
    class C15881 extends BroadcastReceiver {
        C15881() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DeskClockAction.this.getAlertAction())) {
                DeskClockAction.this.mAlertFiring = true;
            } else if (intent.getAction().equals(DeskClockAction.this.getDoneAction())) {
                DeskClockAction.this.mAlertFiring = false;
            }
            DeskClockAction.this.notifyListener();
        }
    }

    DeskClockAction(Context context) {
        super(context, null);
        updateBroadcastReceiver();
        this.mSettingsObserver = new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new _$$Lambda$DeskClockAction$dyH9jy2GURTsOoYs4WoZlKMC29A(this));
    }

    protected void updateBroadcastReceiver() {
        boolean z = false;
        this.mAlertFiring = false;
        if (this.mReceiverRegistered) {
            getContext().unregisterReceiver(this.mAlertReceiver);
            this.mReceiverRegistered = false;
        }
        //if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) != 0) {
            z = true;
        //}
        if (z) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getAlertAction());
            intentFilter.addAction(getDoneAction());
            getContext().registerReceiverAsUser(this.mAlertReceiver, UserHandle.CURRENT, intentFilter, "com.android.systemui.permission.SEND_ALERT_BROADCASTS", null);
            this.mReceiverRegistered = true;
        }
        notifyListener();
    }

    protected abstract Intent createDismissIntent();

    protected abstract String getAlertAction();

    protected abstract String getDoneAction();

    public boolean isAvailable() {
        return this.mAlertFiring;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        try {
            Intent createDismissIntent = createDismissIntent();
            ActivityOptions makeBasic = ActivityOptions.makeBasic();
            makeBasic.setDisallowEnterPictureInPictureWhileLaunching(true);
            createDismissIntent.setFlags(268435456);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("android-app://");
            stringBuilder.append(getContext().getPackageName());
            createDismissIntent.putExtra("android.intent.extra.REFERRER", Uri.parse(stringBuilder.toString()));
            getContext().startActivityAsUser(createDismissIntent, makeBasic.toBundle(), UserHandle.CURRENT);
        } catch (Throwable e) {
            Log.e("Elmyra/DeskClockAction", "Failed to dismiss alert", e);
        }
        this.mAlertFiring = false;
        notifyListener();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mReceiverRegistered -> ");
        stringBuilder.append(this.mReceiverRegistered);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
