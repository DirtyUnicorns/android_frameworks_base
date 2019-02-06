package com.google.android.systemui.elmyra;

import android.content.Context;
import android.metrics.LogMaker;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.Action.Listener;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ElmyraService implements Dumpable {
    protected final Listener mActionListener = new C15821();
    private final List<Action> mActions;
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects;
    protected final Gate.Listener mGateListener = new C15832();
    private final List<Gate> mGates;
    private final GestureSensor.Listener mGestureListener = new GestureListener(this, null);
    private final GestureSensor mGestureSensor;
    private Action mLastActiveAction;
    private long mLastPrimedGesture;
    private int mLastStage;
    private final MetricsLogger mLogger;
    private final PowerManager mPowerManager;
    private final WakeLock mWakeLock;

    /* renamed from: com.google.android.systemui.elmyra.ElmyraService$1 */
    class C15821 implements Listener {
        C15821() {
        }

        public void onActionAvailabilityChanged(Action action) {
            ElmyraService.this.updateSensorListener();
        }
    }

    /* renamed from: com.google.android.systemui.elmyra.ElmyraService$2 */
    class C15832 implements Gate.Listener {
        C15832() {
        }

        public void onGateChanged(Gate gate) {
            ElmyraService.this.updateSensorListener();
        }
    }

    private class GestureListener implements GestureSensor.Listener {
        private GestureListener() {
        }

        /* synthetic */ GestureListener(ElmyraService elmyraService, C15821 c15821) {
            this();
        }

        public void onGestureDetected(GestureSensor gestureSensor, DetectionProperties detectionProperties) {
            ElmyraService.this.mWakeLock.acquire(2000);
            boolean isInteractive = ElmyraService.this.mPowerManager.isInteractive();
            int i = (detectionProperties == null || !detectionProperties.isHostSuspended()) ? !isInteractive ? 2 : 1 : 3;
            LogMaker latency = new LogMaker(999).setType(4).setSubtype(i).setLatency(isInteractive ? SystemClock.uptimeMillis() - ElmyraService.this.mLastPrimedGesture : 0);
            ElmyraService.this.mLastPrimedGesture = 0;
            Action access$100 = ElmyraService.this.updateActiveAction();
            if (access$100 != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Triggering ");
                stringBuilder.append(access$100);
                Log.i("Elmyra/ElmyraService", stringBuilder.toString());
                access$100.onTrigger(detectionProperties);
                i = 0;
                while (true) {
                    int i2 = i;
                    if (i2 >= ElmyraService.this.mFeedbackEffects.size()) {
                        break;
                    }
                    ((FeedbackEffect) ElmyraService.this.mFeedbackEffects.get(i2)).onResolve(detectionProperties);
                    i = i2 + 1;
                }
                latency.setPackageName(access$100.getClass().getName());
            }
            ElmyraService.this.mLogger.write(latency);
        }

        public void onGestureProgress(GestureSensor gestureSensor, float f, int i) {
            Action access$100 = ElmyraService.this.updateActiveAction();
            if (access$100 != null) {
                access$100.onProgress(f, i);
                int i2 = 0;
                while (true) {
                    int i3 = i2;
                    if (i3 >= ElmyraService.this.mFeedbackEffects.size()) {
                        break;
                    }
                    ((FeedbackEffect) ElmyraService.this.mFeedbackEffects.get(i3)).onProgress(f, i);
                    i2 = i3 + 1;
                }
            }
            if (i != ElmyraService.this.mLastStage) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (i == 2) {
                    ElmyraService.this.mLogger.action(998);
                    ElmyraService.this.mLastPrimedGesture = uptimeMillis;
                } else if (i == 0 && ElmyraService.this.mLastPrimedGesture != 0) {
                    ElmyraService.this.mLogger.write(new LogMaker(997).setType(4).setLatency(uptimeMillis - ElmyraService.this.mLastPrimedGesture));
                }
                ElmyraService.this.mLastStage = i;
            }
        }
    }

    public ElmyraService(Context context, ServiceConfiguration serviceConfiguration) {
        this.mContext = context;
        this.mLogger = new MetricsLogger();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mWakeLock = this.mPowerManager.newWakeLock(1, "Elmyra/ElmyraService");
        this.mActions = new ArrayList(serviceConfiguration.getActions());
        this.mActions.forEach(new _$$Lambda$ElmyraService$AV8onMO5IkvT88F5MAxNGAFWl18(this));
        this.mFeedbackEffects = new ArrayList(serviceConfiguration.getFeedbackEffects());
        this.mGates = new ArrayList(serviceConfiguration.getGates());
        this.mGates.forEach(new _$$Lambda$ElmyraService$BALyMaTEhjk9LjmmSMkHO_yFKc4(this));
        this.mGestureSensor = serviceConfiguration.getGestureSensor();
        if (this.mGestureSensor != null) {
            this.mGestureSensor.setGestureListener(this.mGestureListener);
        }
        updateSensorListener();
    }

    private void activateGates() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.mGates.size()) {
                ((Gate) this.mGates.get(i2)).activate();
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private Gate blockingGate() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.mGates.size()) {
                return null;
            }
            if (((Gate) this.mGates.get(i2)).isBlocking()) {
                return (Gate) this.mGates.get(i2);
            }
            i = i2 + 1;
        }
    }

    private void deactivateGates() {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < this.mGates.size()) {
                ((Gate) this.mGates.get(i2)).deactivate();
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    private Action firstAvailableAction() {
        // int i = 0;
        // while (true) {
        //     int i2 = i;
        //     if (i2 >= this.mActions.size()) {
        //         return null;
        //     }
        //     if (((Action) this.mActions.get(i2)).isAvailable()) {
        //         return (Action) this.mActions.get(i2);
        //     }
        //     i = i2 + 1;
        // }
        return (Action) this.mActions.get(0);
    }

    private void startListening() {
        if (this.mGestureSensor != null && !this.mGestureSensor.isListening()) {
            this.mGestureSensor.startListening();
        }
    }

    private void stopListening() {
        if (this.mGestureSensor != null && this.mGestureSensor.isListening()) {
            this.mGestureSensor.stopListening();
            for (int i = 0; i < this.mFeedbackEffects.size(); i++) {
                ((FeedbackEffect) this.mFeedbackEffects.get(i)).onRelease();
            }
            Action updateActiveAction = updateActiveAction();
            if (updateActiveAction != null) {
                updateActiveAction.onProgress(0.0f, 0);
            }
        }
    }

    private Action updateActiveAction() {
        Action firstAvailableAction = firstAvailableAction();
        if (!(this.mLastActiveAction == null || firstAvailableAction == this.mLastActiveAction)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Switching action from ");
            stringBuilder.append(this.mLastActiveAction);
            stringBuilder.append(" to ");
            stringBuilder.append(firstAvailableAction);
            Log.i("Elmyra/ElmyraService", stringBuilder.toString());
            this.mLastActiveAction.onProgress(0.0f, 0);
        }
        this.mLastActiveAction = firstAvailableAction;
        return firstAvailableAction;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int i;
        int i2 = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ElmyraService.class.getSimpleName());
        stringBuilder.append(" state:");
        printWriter.println(stringBuilder.toString());
        printWriter.println("  Gates:");
        for (i = 0; i < this.mGates.size(); i++) {
            printWriter.print("    ");
            if (((Gate) this.mGates.get(i)).isActive()) {
                printWriter.print(((Gate) this.mGates.get(i)).isBlocking() ? "X " : "O ");
            } else {
                printWriter.print("- ");
            }
            printWriter.println(((Gate) this.mGates.get(i)).toString());
        }
        printWriter.println("  Actions:");
        for (i = 0; i < this.mActions.size(); i++) {
            printWriter.print("    ");
            printWriter.print(((Action) this.mActions.get(i)).isAvailable() ? "O " : "X ");
            printWriter.println(((Action) this.mActions.get(i)).toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("  Active: ");
        stringBuilder.append(this.mLastActiveAction);
        printWriter.println(stringBuilder.toString());
        printWriter.println("  Feedback Effects:");
        while (i2 < this.mFeedbackEffects.size()) {
            printWriter.print("    ");
            printWriter.println(((FeedbackEffect) this.mFeedbackEffects.get(i2)).toString());
            i2++;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("  Gesture Sensor: ");
        stringBuilder.append(this.mGestureSensor.toString());
        printWriter.println(stringBuilder.toString());
        if (this.mGestureSensor instanceof Dumpable) {
            ((Dumpable) this.mGestureSensor).dump(fileDescriptor, printWriter, strArr);
        }
    }

    protected void updateSensorListener() {
        Action updateActiveAction = updateActiveAction();
        if (updateActiveAction == null) {
            Log.i("Elmyra/ElmyraService", "No available actions");
            deactivateGates();
            stopListening();
            return;
        }
        activateGates();
        Gate blockingGate = blockingGate();
        if (blockingGate != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Gated by ");
            stringBuilder.append(blockingGate);
            Log.i("Elmyra/ElmyraService", stringBuilder.toString());
            stopListening();
            return;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("Unblocked; current action: ");
        stringBuilder2.append(updateActiveAction);
        Log.i("Elmyra/ElmyraService", stringBuilder2.toString());
        startListening();
    }
}
