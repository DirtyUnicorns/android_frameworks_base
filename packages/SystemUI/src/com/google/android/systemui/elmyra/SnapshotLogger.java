package com.google.android.systemui.elmyra;

import android.os.Binder;
import com.android.systemui.Dumpable;
import com.google.android.systemui.elmyra.proto.nano.ElmyraChassis.SensorEvent;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SnapshotLogger implements Dumpable {
    private final int mSnapshotCapacity;
    private List<Snapshot> mSnapshots;

    public class Snapshot {
        final com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.Snapshot mSnapshot;
        final long mTimestamp;

        Snapshot(com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.Snapshot snapshot, long j) {
            this.mSnapshot = snapshot;
            this.mTimestamp = j;
        }

        public com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.Snapshot getSnapshot() {
            return this.mSnapshot;
        }

        long getTimestamp() {
            return this.mTimestamp;
        }
    }

    public SnapshotLogger(int i) {
        this.mSnapshotCapacity = i;
        this.mSnapshots = new ArrayList(i);
    }

    private void dumpInternal(PrintWriter printWriter) {
        printWriter.println("Dumping Elmyra Snapshots");
        for (int i = 0; i < this.mSnapshots.size(); i++) {
            com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.Snapshot snapshot = ((Snapshot) this.mSnapshots.get(i)).getSnapshot();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SystemTime: ");
            stringBuilder.append(((Snapshot) this.mSnapshots.get(i)).getTimestamp());
            printWriter.println(stringBuilder.toString());
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Snapshot: ");
            stringBuilder2.append(i);
            printWriter.println(stringBuilder2.toString());
            printWriter.print("header {");
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  identifier: ");
            stringBuilder2.append(snapshot.header.identifier);
            printWriter.print(stringBuilder2.toString());
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  gesture_type: ");
            stringBuilder2.append(snapshot.header.gestureType);
            printWriter.print(stringBuilder2.toString());
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("  feedback: ");
            stringBuilder2.append(snapshot.header.feedback);
            printWriter.print(stringBuilder2.toString());
            printWriter.print("}");
            for (int i2 = 0; i2 < snapshot.events.length; i2++) {
                printWriter.print("events {");
                if (snapshot.events[i2].hasGestureStage()) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("  gesture_stage: ");
                    stringBuilder2.append(snapshot.events[i2].getGestureStage());
                    printWriter.print(stringBuilder2.toString());
                } else if (snapshot.events[i2].hasSensorEvent()) {
                    SensorEvent sensorEvent = snapshot.events[i2].getSensorEvent();
                    printWriter.print("  sensor_event {");
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("    timestamp: ");
                    stringBuilder2.append(sensorEvent.timestamp);
                    printWriter.print(stringBuilder2.toString());
                    for (float append : sensorEvent.values) {
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("    values: ");
                        stringBuilder3.append(append);
                        printWriter.print(stringBuilder3.toString());
                    }
                    printWriter.print("  }");
                }
                printWriter.print("}");
            }
            printWriter.println();
        }
        this.mSnapshots.clear();
        printWriter.println("Finished Dumping Elmyra Snapshots");
    }

    public void addSnapshot(com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.Snapshot snapshot, long j) {
        if (this.mSnapshots.size() == this.mSnapshotCapacity) {
            this.mSnapshots.remove(0);
        }
        this.mSnapshots.add(new Snapshot(snapshot, j));
    }

    public void didReceiveQuery() {
        if (this.mSnapshots.size() > 0) {
            ((Snapshot) this.mSnapshots.get(this.mSnapshots.size() - 1)).getSnapshot().header.feedback = 1;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            dumpInternal(printWriter);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public List<Snapshot> getSnapshots() {
        return this.mSnapshots;
    }
}
