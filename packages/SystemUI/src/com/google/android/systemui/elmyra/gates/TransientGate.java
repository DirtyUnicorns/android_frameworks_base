package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.Handler;

abstract class TransientGate extends Gate {
    private final long mBlockDuration;
    private boolean mIsBlocking;
    private final Runnable mResetGate = new C15981();
    private final Handler mResetGateHandler;

    /* renamed from: com.google.android.systemui.elmyra.gates.TransientGate$1 */
    class C15981 implements Runnable {
        C15981() {
        }

        public void run() {
            TransientGate.this.mIsBlocking = false;
            TransientGate.this.notifyListener();
        }
    }

    TransientGate(Context context, long j) {
        super(context);
        this.mBlockDuration = j;
        this.mResetGateHandler = new Handler(context.getMainLooper());
    }

    protected void block() {
        this.mIsBlocking = true;
        notifyListener();
        this.mResetGateHandler.removeCallbacks(this.mResetGate);
        this.mResetGateHandler.postDelayed(this.mResetGate, this.mBlockDuration);
    }

    protected boolean isBlocked() {
        return this.mIsBlocking;
    }
}
