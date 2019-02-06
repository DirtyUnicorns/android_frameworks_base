package com.google.android.systemui.elmyra.sensors;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.R;
import com.google.android.systemui.elmyra.proto.nano.ElmyraChassis.Chassis;
import com.google.android.systemui.elmyra.proto.nano.ElmyraGestureDetector.AggregateDetector;
import com.google.android.systemui.elmyra.proto.nano.ElmyraGestureDetector.SlopeDetector;
import com.google.android.systemui.elmyra.sensors.GestureSensor.Listener;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.SensorCalibration;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class JNIGestureSensor implements GestureSensor {
    private static final String DISABLE_SETTING = "com.google.android.systemui.elmyra.disable_jni";
    private static final int SENSOR_RATE = 20000;
    private static final String TAG = "Elmyra/JNIGestureSensor";
    private static boolean sLibraryLoaded;
    private final Context mContext;
    private final AssistGestureController mController;
    private final GestureConfiguration mGestureConfiguration;
    private boolean mIsListening;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new C16131();
    private long mNativeService;
    private int mSensorCount;
    private final String mSensorStringType;

    /* renamed from: com.google.android.systemui.elmyra.sensors.JNIGestureSensor$1 */
    class C16131 extends KeyguardUpdateMonitorCallback {
        private boolean mWasListening;

        C16131() {
        }

        public void onFinishedGoingToSleep(int i) {
            JNIGestureSensor.this.mController.onGestureProgress(0.0f);
            this.mWasListening = JNIGestureSensor.this.isListening();
            JNIGestureSensor.this.stopListening();
        }

        public void onStartedWakingUp() {
            JNIGestureSensor.this.mController.onGestureProgress(0.0f);
            if (this.mWasListening) {
                JNIGestureSensor.this.startListening();
            }
        }
    }

    static {
        try {
            //System.loadLibrary("elmyra");
            sLibraryLoaded = true;
        } catch (Throwable th) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not load JNI component: ");
            stringBuilder.append(th);
            Log.w(TAG, stringBuilder.toString());
            sLibraryLoaded = false;
        }
    }

    public JNIGestureSensor(Context context, GestureConfiguration gestureConfiguration) {
        this.mContext = context;
        this.mController = new AssistGestureController(context, this);
        this.mSensorStringType = context.getResources().getString(R.string.elmyra_raw_sensor_string_type);
        this.mGestureConfiguration = gestureConfiguration;
        this.mGestureConfiguration.setListener(new _$$Lambda$JNIGestureSensor$_LNLV8OrdpJRbyOEiZGkaj6wYCk(this));
        KeyguardUpdateMonitor.getInstance(context).registerCallback(this.mKeyguardUpdateMonitorCallback);
        byte[] chassisAsset = getChassisAsset(context);
        if (chassisAsset != null && chassisAsset.length != 0) {
            try {
                Chassis chassis = new Chassis();
                MessageNano.mergeFrom(chassis, chassisAsset);
                this.mSensorCount = chassis.sensors.length;
                for (int i = 0; i < this.mSensorCount; i++) {
                    SensorCalibration calibration = SensorCalibration.getCalibration(i);
                    if (calibration == null || !calibration.contains("touch_2_sensitivity")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Error reading calibration for sensor ");
                        stringBuilder.append(i);
                        Log.w(TAG, stringBuilder.toString());
                    } else {
                        chassis.sensors[i].sensitivity = 1.0f / calibration.get("touch_2_sensitivity");
                    }
                }
                createNativeService(chassisAsset);
            } catch (Throwable e) {
                Log.e(TAG, "Error reading chassis file", e);
                this.mSensorCount = 0;
            }
        }
    }

    private native boolean createNativeService(byte[] bArr);

    private native void destroyNativeService();

    private static byte[] getChassisAsset(Context context) {
        Throwable e;
        try {
            return readAllBytes(context.getResources().openRawResource(R.raw.elmyra_chassis));
        } catch (IOException e2) {
            e = e2;
        } catch (NotFoundException e3) {
            e = e3;
        }
        Log.e(TAG, "Could not load chassis resource", e);
        return null;
    }

    public static boolean isAvailable(Context context) {
        if (Secure.getInt(context.getContentResolver(), DISABLE_SETTING, 0) == 1 || !sLibraryLoaded) {
            return false;
        }
        byte[] chassisAsset = getChassisAsset(context);
        return (chassisAsset == null || chassisAsset.length == 0) ? false : true;
    }

    private void onGestureDetected() {
        this.mController.onGestureDetected(null);
    }

    private void onGestureProgress(float f) {
        this.mController.onGestureProgress(f);
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[1024];
        int length = bArr.length;
        int i = 0;
        while (true) {
            int read = inputStream.read(bArr, i, length - i);
            if (read > 0) {
                i += read;
            } else if (read < 0) {
                break;
            } else {
                length <<= 1;
                bArr = Arrays.copyOf(bArr, length);
            }
        }
        return length == i ? bArr : Arrays.copyOf(bArr, i);
    }

    private native boolean setGestureDetector(byte[] bArr);

    private native boolean startListeningNative(String str, int i);

    private native void stopListeningNative();

    protected void updateConfiguration() {
        SlopeDetector slopeDetector = new SlopeDetector();
        slopeDetector.sensitivity = this.mGestureConfiguration.getSlopeSensitivity();
        slopeDetector.upperThreshold = this.mGestureConfiguration.getUpperThreshold();
        slopeDetector.lowerThreshold = this.mGestureConfiguration.getLowerThreshold();
        slopeDetector.releaseThreshold = slopeDetector.upperThreshold * 0.1f;
        slopeDetector.timeThreshold = (long) this.mGestureConfiguration.getTimeWindow();
        AggregateDetector aggregateDetector = new AggregateDetector();
        aggregateDetector.count = this.mSensorCount;
        aggregateDetector.detector = slopeDetector;
        setGestureDetector(MessageNano.toByteArray(aggregateDetector));
    }

    protected void finalize() throws Throwable {
        super.finalize();
        destroyNativeService();
    }

    public boolean isListening() {
        return this.mIsListening;
    }

    public void setGestureListener(Listener listener) {
        this.mController.setGestureListener(listener);
    }

    public void startListening() {
        if (!this.mIsListening && startListeningNative(this.mSensorStringType, SENSOR_RATE)) {
            updateConfiguration();
            this.mIsListening = true;
        }
    }

    public void stopListening() {
        if (this.mIsListening) {
            stopListeningNative();
            this.mIsListening = false;
        }
    }
}
