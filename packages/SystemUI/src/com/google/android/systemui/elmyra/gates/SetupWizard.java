package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.ArrayList;
import java.util.List;

public class SetupWizard extends Gate {
    private final List<Action> mExceptions;
    private final DeviceProvisionedController mProvisionedController;
    private final DeviceProvisionedListener mProvisionedListener = new C16061();
    private boolean mSetupComplete;

    /* renamed from: com.google.android.systemui.elmyra.gates.SetupWizard$1 */
    class C16061 implements DeviceProvisionedListener {
        C16061() {
        }

        private void updateSetupComplete() {
            boolean access$000 = SetupWizard.this.isSetupComplete();
            if (access$000 != SetupWizard.this.mSetupComplete) {
                SetupWizard.this.mSetupComplete = access$000;
                SetupWizard.this.notifyListener();
            }
        }

        public void onDeviceProvisionedChanged() {
            updateSetupComplete();
        }

        public void onUserSetupChanged() {
            updateSetupComplete();
        }
    }

    public SetupWizard(Context context, List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList(list);
        this.mProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);
    }

    private boolean isSetupComplete() {
        return this.mProvisionedController.isDeviceProvisioned() && this.mProvisionedController.isCurrentUserSetup();
    }

    protected boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        return !this.mSetupComplete;
    }

    protected void onActivate() {
        this.mSetupComplete = isSetupComplete();
        this.mProvisionedController.addCallback(this.mProvisionedListener);
    }

    protected void onDeactivate() {
        this.mProvisionedController.removeCallback(this.mProvisionedListener);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [isDeviceProvisioned -> ");
        stringBuilder.append(this.mProvisionedController.isDeviceProvisioned());
        stringBuilder.append("; isCurrentUserSetup -> ");
        stringBuilder.append(this.mProvisionedController.isCurrentUserSetup());
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
