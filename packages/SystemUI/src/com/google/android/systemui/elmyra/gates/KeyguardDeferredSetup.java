package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.provider.Settings.Secure;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.gates.Gate.Listener;
import java.util.ArrayList;
import java.util.List;

public class KeyguardDeferredSetup extends Gate {
    private boolean mDeferredSetupComplete;
    private final List<Action> mExceptions;
    private final KeyguardVisibility mKeyguardGate;
    private final Listener mKeyguardGateListener = new C15991();
    private final UserContentObserver mSettingsObserver;

    /* renamed from: com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup$1 */
    class C15991 implements Listener {
        C15991() {
        }

        public void onGateChanged(Gate gate) {
            KeyguardDeferredSetup.this.notifyListener();
        }
    }

    public KeyguardDeferredSetup(Context context, List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList(list);
        this.mKeyguardGate = new KeyguardVisibility(context);
        this.mKeyguardGate.setListener(this.mKeyguardGateListener);
        this.mSettingsObserver = new UserContentObserver(context, Secure.getUriFor("assist_gesture_setup_complete"), new _$$Lambda$KeyguardDeferredSetup$XgzT2zBsBCjxQnRb5RrgDqiHavM(this), false);
    }

    private boolean isDeferredSetupComplete() {
        return Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_setup_complete", 0, -2) != 0;
    }

    protected void updateSetupComplete() {
        boolean isDeferredSetupComplete = isDeferredSetupComplete();
        if (this.mDeferredSetupComplete != isDeferredSetupComplete) {
            this.mDeferredSetupComplete = isDeferredSetupComplete;
            notifyListener();
        }
    }

    protected boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        return !this.mDeferredSetupComplete && this.mKeyguardGate.isBlocking();
    }

    public boolean isSuwComplete() {
        return this.mDeferredSetupComplete;
    }

    protected void onActivate() {
        this.mKeyguardGate.activate();
        this.mDeferredSetupComplete = isDeferredSetupComplete();
        this.mSettingsObserver.activate();
    }

    protected void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mSettingsObserver.deactivate();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mDeferredSetupComplete -> ");
        stringBuilder.append(this.mDeferredSetupComplete);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
