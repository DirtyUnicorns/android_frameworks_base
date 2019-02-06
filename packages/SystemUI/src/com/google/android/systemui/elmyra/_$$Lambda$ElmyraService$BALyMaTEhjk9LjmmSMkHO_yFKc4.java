package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.gates.Gate;
import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$ElmyraService$BALyMaTEhjk9LjmmSMkHO_yFKc4 implements Consumer {
    private final /* synthetic */ ElmyraService f$0;

    public /* synthetic */ _$$Lambda$ElmyraService$BALyMaTEhjk9LjmmSMkHO_yFKc4(ElmyraService elmyraService) {
        this.f$0 = elmyraService;
    }

    public final void accept(Object obj) {
        ((Gate) obj).setListener(this.f$0.mGateListener);
    }
}
