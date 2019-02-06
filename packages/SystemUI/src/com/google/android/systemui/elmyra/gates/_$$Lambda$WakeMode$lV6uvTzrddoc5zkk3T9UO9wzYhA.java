package com.google.android.systemui.elmyra.gates;

import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$WakeMode$lV6uvTzrddoc5zkk3T9UO9wzYhA implements Consumer {
    private final /* synthetic */ WakeMode f$0;

    public /* synthetic */ _$$Lambda$WakeMode$lV6uvTzrddoc5zkk3T9UO9wzYhA(WakeMode wakeMode) {
        this.f$0 = wakeMode;
    }

    public final void accept(Object obj) {
        this.f$0.updateWakeSetting();
    }
}
