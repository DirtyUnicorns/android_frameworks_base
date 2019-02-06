package com.google.android.systemui.elmyra.sensors;

import android.content.Context;
import android.hardware.location.ContextHubInfo;
import android.hardware.location.ContextHubManager;
import android.hardware.location.ContextHubManager.Callback;
import android.hardware.location.ContextHubMessage;
import android.hardware.location.NanoAppFilter;
import android.util.Log;
import android.util.TypedValue;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.google.android.systemui.elmyra.SnapshotConfiguration;
import com.google.android.systemui.elmyra.SnapshotController;
import com.google.android.systemui.elmyra.proto.nano.CHREMessages.MessageV1;
import com.google.android.systemui.elmyra.proto.nano.ElmyraGestureDetector.AggregateDetector;
import com.google.android.systemui.elmyra.proto.nano.ElmyraGestureDetector.SlopeDetector;
import com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.SnapshotHeader;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CHREGestureSensor implements Dumpable, GestureSensor {
    private final Context mContext;
    private Callback mContextHubCallback = new C16121();
    private int mContextHubHandle;
    private final ContextHubManager mContextHubManager;
    private final AssistGestureController mController;
    private int mFindNanoAppRetries;
    private final GestureConfiguration mGestureConfiguration;
    private boolean mIsListening;
    private boolean mNanoAppFound;
    private final boolean mNanoAppFoundOnBoot;
    private int mNanoAppHandle;
    private final float mProgressDetectThreshold;
    private final SnapshotController.Listener mSnapshotListener = new _$$Lambda$CHREGestureSensor$9PliWiPLg_s_9_ha1Cbnsvp3HuA(this);

    /* renamed from: com.google.android.systemui.elmyra.sensors.CHREGestureSensor$1 */
    class C16121 extends Callback {
        C16121() {
        }

        public void onMessageReceipt(int i, int i2, ContextHubMessage contextHubMessage) {
            if (i2 == CHREGestureSensor.this.mNanoAppHandle) {
                if (CHREGestureSensor.this.mNanoAppFound) {
                    try {
                        if (contextHubMessage.getMsgType() == 1) {
                            MessageV1 parseFrom = MessageV1.parseFrom(contextHubMessage.getData());
                            if (parseFrom.hasGestureDetected()) {
                                CHREGestureSensor.this.mController.onGestureDetected(new DetectionProperties(parseFrom.getGestureDetected().hapticConsumed, parseFrom.getGestureDetected().hostSuspended));
                                return;
                            } else if (parseFrom.hasGestureProgress()) {
                                CHREGestureSensor.this.mController.onGestureProgress(parseFrom.getGestureProgress());
                                return;
                            } else if (parseFrom.hasSnapshot()) {
                                CHREGestureSensor.this.mController.onSnapshotReceived(parseFrom.getSnapshot());
                                return;
                            } else if (parseFrom.hasChassis()) {
                                CHREGestureSensor.this.mController.storeChassisConfiguration(parseFrom.getChassis());
                                return;
                            } else {
                                return;
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unknown message type: ");
                        stringBuilder.append(contextHubMessage.getMsgType());
                        Log.e("Elmyra/CHREGestureSensor", stringBuilder.toString());
                        return;
                    } catch (Throwable e) {
                        Log.e("Elmyra/CHREGestureSensor", "Invalid protocol buffer", e);
                        return;
                    }
                }
                Log.wtf("Elmyra/CHREGestureSensor", "onMessageReceipt(): nanoapp not found");
            }
        }
    }

    public CHREGestureSensor(Context context, GestureConfiguration gestureConfiguration, SnapshotConfiguration snapshotConfiguration) {
        this.mContext = context;
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(R.dimen.elmyra_progress_detect_threshold, typedValue, true);
        this.mProgressDetectThreshold = typedValue.getFloat();
        this.mController = new AssistGestureController(context, this, snapshotConfiguration);
        this.mController.setSnapshotListener(this.mSnapshotListener);
        this.mGestureConfiguration = gestureConfiguration;
        this.mGestureConfiguration.setListener(new _$$Lambda$CHREGestureSensor$GZmCEPt8kQ_zIdv2oTQazqe1swY(this));
        this.mContextHubManager = (ContextHubManager) this.mContext.getSystemService("contexthub");
        this.mNanoAppFoundOnBoot = findNanoApp();
    }

    private byte[] buildGestureDetectorMessage() throws IOException {
        SlopeDetector slopeDetector = new SlopeDetector();
        slopeDetector.sensitivity = this.mGestureConfiguration.getSlopeSensitivity();
        slopeDetector.upperThreshold = this.mGestureConfiguration.getUpperThreshold();
        slopeDetector.lowerThreshold = this.mGestureConfiguration.getLowerThreshold();
        slopeDetector.releaseThreshold = slopeDetector.upperThreshold * 0.1f;
        slopeDetector.timeThreshold = (long) this.mGestureConfiguration.getTimeWindow();
        AggregateDetector aggregateDetector = new AggregateDetector();
        aggregateDetector.count = 6;
        aggregateDetector.detector = slopeDetector;
        MessageV1 messageV1 = new MessageV1();
        messageV1.setAggregateDetector(aggregateDetector);
        return serializeProtobuf(messageV1);
    }

    private byte[] buildProgressReportThresholdMessage() throws IOException {
        MessageV1 messageV1 = new MessageV1();
        messageV1.setProgressReportThreshold(this.mProgressDetectThreshold);
        return serializeProtobuf(messageV1);
    }

    private byte[] buildRecognizerStartMessage(boolean z) throws IOException {
        MessageV1 messageV1 = new MessageV1();
        messageV1.setRecognizerStart(z);
        return serializeProtobuf(messageV1);
    }

    private byte[] buildRequestCalibrationMessage() throws IOException {
        MessageV1 messageV1 = new MessageV1();
        messageV1.setCalibrationRequest(true);
        return serializeProtobuf(messageV1);
    }

    private byte[] buildRequestSnapshotMessage(SnapshotHeader snapshotHeader) throws IOException {
        MessageV1 messageV1 = new MessageV1();
        messageV1.setSnapshotRequest(snapshotHeader);
        return serializeProtobuf(messageV1);
    }

    private boolean findNanoApp() {
        if (this.mNanoAppFound) {
            return true;
        }
        this.mFindNanoAppRetries++;
        List contextHubs = this.mContextHubManager.getContextHubs();
        if (contextHubs.size() == 0) {
            Log.e("Elmyra/CHREGestureSensor", "No context hubs found");
            return false;
        }
        this.mContextHubHandle = ((ContextHubInfo) contextHubs.get(0)).getId();
        try {
            this.mContextHubManager.queryNanoApps((ContextHubInfo) contextHubs.get(0)).waitForResponse(5, TimeUnit.SECONDS);
            int[] findNanoAppOnHub = this.mContextHubManager.findNanoAppOnHub(-1, new NanoAppFilter(5147455389092024334L, 1, 0, 306812249964L));
            if (findNanoAppOnHub.length != 1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid number of handles: ");
                stringBuilder.append(findNanoAppOnHub.length);
                Log.e("Elmyra/CHREGestureSensor", stringBuilder.toString());
                return false;
            }
            this.mNanoAppFound = true;
            this.mNanoAppHandle = findNanoAppOnHub[0];
            return true;
        } catch (InterruptedException e) {
            Log.e("Elmyra/CHREGestureSensor", "Interrupted while looking for nanoapp");
            return false;
        } catch (TimeoutException e2) {
            Log.e("Elmyra/CHREGestureSensor", "Timed out looking for nanoapp");
            return false;
        }
    }

    private void requestCalibration() {
        try {
            sendMessageToNanoApp(new ContextHubMessage(1, -1, buildRequestCalibrationMessage()));
        } catch (Throwable e) {
            Log.e("Elmyra/CHREGestureSensor", "Unable to serialize calibration request message", e);
        }
    }

    protected void requestSnapshot(SnapshotHeader snapshotHeader) {
        try {
            sendMessageToNanoApp(new ContextHubMessage(1, -1, buildRequestSnapshotMessage(snapshotHeader)));
        } catch (Throwable e) {
            Log.e("Elmyra/CHREGestureSensor", "Unable to serialize snapshot request proto", e);
        }
    }

    private byte[] serializeProtobuf(MessageV1 messageV1) throws IOException {
        byte[] bArr = new byte[messageV1.getSerializedSize()];
        messageV1.writeTo(CodedOutputByteBufferNano.newInstance(bArr));
        return bArr;
    }

    protected void updateSensorConfiguration() {
        if (!this.mNanoAppFound) {
            boolean findNanoApp = findNanoApp();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error: updateSensorConfiguration(): nanoapp not found, refind = ");
            stringBuilder.append(findNanoApp);
            Log.e("Elmyra/CHREGestureSensor", stringBuilder.toString());
            if (!findNanoApp) {
                return;
            }
        }
        try {
            sendMessageToNanoApp(new ContextHubMessage(1, -1, buildGestureDetectorMessage()));
            sendMessageToNanoApp(new ContextHubMessage(1, -1, buildProgressReportThresholdMessage()));
        } catch (Throwable e) {
            Log.e("Elmyra/CHREGestureSensor", "Unable to update sensitivity", e);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CHREGestureSensor.class.getSimpleName());
        stringBuilder.append(" state:");
        printWriter.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("  mIsListening: ");
        stringBuilder.append(this.mIsListening);
        printWriter.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("  mNanoAppFound: ");
        stringBuilder.append(this.mNanoAppFound);
        printWriter.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("  mNanoAppFoundOnBoot: ");
        stringBuilder.append(this.mNanoAppFoundOnBoot);
        printWriter.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("  mFindNanoAppRetries: ");
        stringBuilder.append(this.mFindNanoAppRetries);
        printWriter.println(stringBuilder.toString());
        this.mController.dump(fileDescriptor, printWriter, strArr);
    }

    public boolean isListening() {
        return this.mIsListening;
    }

    void sendMessageToNanoApp(ContextHubMessage contextHubMessage) {
        if (this.mContextHubManager.sendMessage(this.mContextHubHandle, this.mNanoAppHandle, contextHubMessage) != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to send message (");
            stringBuilder.append(contextHubMessage.getMsgType());
            stringBuilder.append(") to mNanoAppHandle: ");
            stringBuilder.append(this.mNanoAppHandle);
            Log.e("Elmyra/CHREGestureSensor", stringBuilder.toString());
        }
    }

    public void setGestureListener(GestureSensor.Listener listener) {
        this.mController.setGestureListener(listener);
    }

    public void startListening() {
        if (!this.mNanoAppFound) {
            boolean findNanoApp = findNanoApp();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("startListening(): nanoapp not found, refind = ");
            stringBuilder.append(findNanoApp);
            Log.e("Elmyra/CHREGestureSensor", stringBuilder.toString());
            if (!findNanoApp) {
                return;
            }
        }
        if (!this.mIsListening) {
            updateSensorConfiguration();
            try {
                sendMessageToNanoApp(new ContextHubMessage(1, -1, buildRecognizerStartMessage(true)));
                this.mIsListening = true;
                this.mContextHubManager.registerCallback(this.mContextHubCallback);
            } catch (Throwable e) {
                Log.e("Elmyra/CHREGestureSensor", "Unable to serialize start proto", e);
            }
            if (this.mController.getChassisConfiguration() == null) {
                requestCalibration();
            }
        }
    }

    public void stopListening() {
        if (!this.mNanoAppFound) {
            boolean findNanoApp = findNanoApp();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("stopListening(): nanoapp not found, refind = ");
            stringBuilder.append(findNanoApp);
            Log.e("Elmyra/CHREGestureSensor", stringBuilder.toString());
            if (!findNanoApp) {
                return;
            }
        }
        if (this.mIsListening) {
            try {
                sendMessageToNanoApp(new ContextHubMessage(1, -1, buildRecognizerStartMessage(false)));
                this.mContextHubManager.unregisterCallback(this.mContextHubCallback);
                this.mIsListening = false;
            } catch (Throwable e) {
                Log.e("Elmyra/CHREGestureSensor", "Unable to serialize stop proto", e);
            }
        }
    }
}
