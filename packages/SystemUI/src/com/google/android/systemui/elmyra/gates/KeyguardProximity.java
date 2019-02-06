package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.util.AsyncSensorManager;
import com.google.android.systemui.elmyra.gates.Gate.Listener;

public class KeyguardProximity extends Gate {
    private final Listener mGateListener = new C16001();
    private boolean mIsListening = false;
    private final KeyguardVisibility mKeyguardGate;
    private boolean mProximityBlocked = false;
    private final float mProximityThreshold;
    private final SensorManager mSensorManager = ((SensorManager) Dependency.get(AsyncSensorManager.class));
    private final Sensor mSensor = this.mSensorManager.getDefaultSensor(8);
    private final SensorEventListener mSensorListener = new C16012();


    /* renamed from: com.google.android.systemui.elmyra.gates.KeyguardProximity$1 */
    class C16001 implements Listener {
        C16001() {
        }

        public void onGateChanged(Gate gate) {
            KeyguardProximity.this.updateProximityListener();
        }
    }

    /* renamed from: com.google.android.systemui.elmyra.gates.KeyguardProximity$2 */
    class C16012 implements SensorEventListener {
        C16012() {
        }

        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            boolean z = false;
            if (sensorEvent.values[0] < KeyguardProximity.this.mProximityThreshold) {
                z = true;
            }
            if (KeyguardProximity.this.mIsListening && z != KeyguardProximity.this.mProximityBlocked) {
                KeyguardProximity.this.mProximityBlocked = z;
                KeyguardProximity.this.notifyListener();
            }
        }
    }

    public KeyguardProximity(Context context) {
        super(context);
        if (this.mSensor == null) {
            this.mProximityThreshold = 0.0f;
            this.mKeyguardGate = null;
            Log.e("Elmyra/KeyguardProximity", "Could not find any Sensor.TYPE_PROXIMITY");
            return;
        }
        this.mProximityThreshold = Math.min(this.mSensor.getMaximumRange(), (float) context.getResources().getInteger(R.integer.elmyra_keyguard_proximity_threshold));
        this.mKeyguardGate = new KeyguardVisibility(context);
        this.mKeyguardGate.setListener(this.mGateListener);
        updateProximityListener();
    }

    private void updateProximityListener() {
        if (this.mProximityBlocked) {
            this.mProximityBlocked = false;
            notifyListener();
        }
        if (!isActive() || !this.mKeyguardGate.isKeyguardShowing() || this.mKeyguardGate.isKeyguardOccluded()) {
            this.mSensorManager.unregisterListener(this.mSensorListener);
            this.mIsListening = false;
        } else if (!this.mIsListening) {
            this.mSensorManager.registerListener(this.mSensorListener, this.mSensor, 3);
            this.mIsListening = true;
        }
    }

    protected boolean isBlocked() {
        return this.mIsListening && this.mProximityBlocked;
    }

    protected void onActivate() {
        if (this.mSensor != null) {
            this.mKeyguardGate.activate();
            updateProximityListener();
        }
    }

    protected void onDeactivate() {
        if (this.mSensor != null) {
            this.mKeyguardGate.deactivate();
            updateProximityListener();
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mIsListening -> ");
        stringBuilder.append(this.mIsListening);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
