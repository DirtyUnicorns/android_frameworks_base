package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.google.android.systemui.elmyra.actions.Action;
import java.util.ArrayList;
import java.util.List;

public class NavigationBarVisibility extends Gate {
    private final CommandQueue mCommandQueue;
    private final Callbacks mCommandQueueCallbacks = new C16031();
    private final List<Action> mExceptions;
    private boolean mIsNavigationHidden;

    /* renamed from: com.google.android.systemui.elmyra.gates.NavigationBarVisibility$1 */
    class C16031 implements Callbacks {
        C16031() {
        }

        public void setWindowState(int i, int i2) {
            if (i == 2) {
                boolean z = i2 != 0;
                if (z != NavigationBarVisibility.this.mIsNavigationHidden) {
                    NavigationBarVisibility.this.mIsNavigationHidden = z;
                    NavigationBarVisibility.this.notifyListener();
                }
            }
        }
    }

    public NavigationBarVisibility(Context context, List<Action> list) {
        super(context);
        this.mExceptions = new ArrayList(list);
        this.mIsNavigationHidden = false;
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
        this.mCommandQueue.addCallbacks(this.mCommandQueueCallbacks);
    }

    protected boolean isBlocked() {
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        return this.mIsNavigationHidden;
    }

    protected void onActivate() {
    }

    protected void onDeactivate() {
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(super.toString());
        stringBuilder.append(" [mIsNavigationHidden -> ");
        stringBuilder.append(this.mIsNavigationHidden);
        stringBuilder.append("; mExceptions -> ");
        stringBuilder.append(this.mExceptions);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
