package com.google.android.systemui.elmyra.gates;

import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$KeyguardDeferredSetup$XgzT2zBsBCjxQnRb5RrgDqiHavM implements Consumer {
    private final /* synthetic */ KeyguardDeferredSetup f$0;

    public /* synthetic */ _$$Lambda$KeyguardDeferredSetup$XgzT2zBsBCjxQnRb5RrgDqiHavM(KeyguardDeferredSetup keyguardDeferredSetup) {
        this.f$0 = keyguardDeferredSetup;
    }

    public final void accept(Object obj) {
        this.f$0.updateSetupComplete();
    }
}
