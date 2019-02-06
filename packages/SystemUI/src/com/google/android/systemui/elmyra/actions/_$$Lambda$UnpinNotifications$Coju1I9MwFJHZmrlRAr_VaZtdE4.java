package com.google.android.systemui.elmyra.actions;

import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$UnpinNotifications$Coju1I9MwFJHZmrlRAr_VaZtdE4 implements Consumer {
    private final /* synthetic */ UnpinNotifications f$0;

    public /* synthetic */ _$$Lambda$UnpinNotifications$Coju1I9MwFJHZmrlRAr_VaZtdE4(UnpinNotifications unpinNotifications) {
        this.f$0 = unpinNotifications;
    }

    public final void accept(Object obj) {
        this.f$0.updateHeadsUpListener();
    }
}
