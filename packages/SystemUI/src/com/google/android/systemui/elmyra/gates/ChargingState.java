package com.google.android.systemui.elmyra.gates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.systemui.R;

public class ChargingState extends TransientGate {
    private final BroadcastReceiver mPowerReceiver = new C15971();

    /* renamed from: com.google.android.systemui.elmyra.gates.ChargingState$1 */
    class C15971 extends BroadcastReceiver {
        C15971() {
        }

        public void onReceive(Context context, Intent intent) {
            ChargingState.this.block();
        }
    }

    public ChargingState(Context context) {
        super(context, (long) context.getResources().getInteger(R.integer.elmyra_charging_gate_duration));
    }

    protected void onActivate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        getContext().registerReceiver(this.mPowerReceiver, intentFilter);
    }

    protected void onDeactivate() {
        getContext().unregisterReceiver(this.mPowerReceiver);
    }
}
