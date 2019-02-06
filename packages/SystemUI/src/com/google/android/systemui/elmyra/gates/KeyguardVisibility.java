package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import com.android.systemui.statusbar.policy.KeyguardMonitor.Callback;

public class KeyguardVisibility extends Gate {
    private final KeyguardMonitor mKeyguardMonitor = ((KeyguardMonitor) Dependency.get(KeyguardMonitor.class));
    private final Callback mKeyguardMonitorCallback = new C16021();

    /* renamed from: com.google.android.systemui.elmyra.gates.KeyguardVisibility$1 */
    class C16021 implements Callback {
        C16021() {
        }

        public void onKeyguardShowingChanged() {
            KeyguardVisibility.this.notifyListener();
        }
    }

    public KeyguardVisibility(Context context) {
        super(context);
    }

    protected boolean isBlocked() {
        return isKeyguardShowing();
    }

    public boolean isKeyguardOccluded() {
        return this.mKeyguardMonitor.isOccluded();
    }

    public boolean isKeyguardShowing() {
        return this.mKeyguardMonitor.isShowing();
    }

    protected void onActivate() {
        this.mKeyguardMonitor.addCallback(this.mKeyguardMonitorCallback);
    }

    protected void onDeactivate() {
        this.mKeyguardMonitor.removeCallback(this.mKeyguardMonitorCallback);
    }
}
