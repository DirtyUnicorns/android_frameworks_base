package com.google.android.systemui.elmyra.actions;

import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$SilenceCall$P91IyaoSIoRZpeDIyPp8173JrBg implements Consumer {
    private final /* synthetic */ SilenceCall f$0;

    public /* synthetic */ _$$Lambda$SilenceCall$P91IyaoSIoRZpeDIyPp8173JrBg(SilenceCall silenceCall) {
        this.f$0 = silenceCall;
    }

    public final void accept(Object obj) {
        this.f$0.updatePhoneStateListener();
    }
}
