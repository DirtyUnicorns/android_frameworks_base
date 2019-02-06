package com.google.android.systemui.elmyra.sensors;

import com.google.android.systemui.elmyra.SnapshotController.Listener;
import com.google.android.systemui.elmyra.proto.nano.SnapshotMessages.SnapshotHeader;

public final /* synthetic */ class _$$Lambda$CHREGestureSensor$9PliWiPLg_s_9_ha1Cbnsvp3HuA implements Listener {
    private final /* synthetic */ CHREGestureSensor f$0;

    public /* synthetic */ _$$Lambda$CHREGestureSensor$9PliWiPLg_s_9_ha1Cbnsvp3HuA(CHREGestureSensor cHREGestureSensor) {
        this.f$0 = cHREGestureSensor;
    }

    public final void onSnapshotRequested(SnapshotHeader snapshotHeader) {
        this.f$0.requestSnapshot(snapshotHeader);
    }
}
