package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.content.Intent;

public class DismissTimer extends DeskClockAction {
    public DismissTimer(Context context) {
        super(context);
    }

    protected Intent createDismissIntent() {
        return new Intent("android.intent.action.DISMISS_TIMER");
    }

    protected String getAlertAction() {
        return "com.google.android.deskclock.action.TIMER_ALERT";
    }

    protected String getDoneAction() {
        return "com.google.android.deskclock.action.TIMER_DONE";
    }
}
