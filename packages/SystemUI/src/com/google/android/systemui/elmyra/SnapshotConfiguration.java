package com.google.android.systemui.elmyra;

import android.content.Context;
import com.android.systemui.R;

public class SnapshotConfiguration {
    private final int mCompleteGestures;
    private final int mIncompleteGestures;
    private final int mSnapshotDelayAfterGesture;

    public SnapshotConfiguration() {
        this.mCompleteGestures = 0;
        this.mIncompleteGestures = 0;
        this.mSnapshotDelayAfterGesture = 0;
    }

    public SnapshotConfiguration(Context context) {
        this.mCompleteGestures = context.getResources().getInteger(R.integer.elmyra_snapshot_logger_gesture_size);
        this.mIncompleteGestures = context.getResources().getInteger(R.integer.elmyra_snapshot_logger_incomplete_gesture_size);
        this.mSnapshotDelayAfterGesture = context.getResources().getInteger(R.integer.elmyra_snapshot_gesture_delay_ms);
    }

    public int getCompleteGestures() {
        return this.mCompleteGestures;
    }

    public int getIncompleteGestures() {
        return this.mIncompleteGestures;
    }

    public int getSnapshotDelayAfterGesture() {
        return this.mSnapshotDelayAfterGesture;
    }
}
