package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.content.Intent;

public class SnoozeAlarm extends DeskClockAction {
    public SnoozeAlarm(Context context) {
        super(context);
    }

    protected Intent createDismissIntent() {
        return new Intent("android.intent.action.SNOOZE_ALARM");
    }

    protected String getAlertAction() {
        return "com.google.android.deskclock.action.ALARM_ALERT";
    }

    protected String getDoneAction() {
        return "com.google.android.deskclock.action.ALARM_DONE";
    }
}
