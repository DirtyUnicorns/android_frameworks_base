package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.R;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;

public class SystemKeyPress extends TransientGate {
    private final int[] mBlockingKeys;
    private final CommandQueue mCommandQueue;
    private final Callbacks mCommandQueueCallbacks = new C16071();

    /* renamed from: com.google.android.systemui.elmyra.gates.SystemKeyPress$1 */
    class C16071 implements Callbacks {
        C16071() {
        }

        public void handleSystemKey(int i) {
            for (int i2 : SystemKeyPress.this.mBlockingKeys) {
                if (i2 == i) {
                    SystemKeyPress.this.block();
                    return;
                }
            }
        }
    }

    public SystemKeyPress(Context context) {
        super(context, (long) context.getResources().getInteger(R.integer.elmyra_system_key_gate_duration));
        this.mBlockingKeys = context.getResources().getIntArray(R.array.elmyra_blocking_system_keys);
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
    }

    protected void onActivate() {
        this.mCommandQueue.addCallbacks(this.mCommandQueueCallbacks);
    }

    protected void onDeactivate() {
        this.mCommandQueue.removeCallbacks(this.mCommandQueueCallbacks);
    }
}
