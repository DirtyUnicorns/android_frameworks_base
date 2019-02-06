package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.actions.Action;
import java.util.function.Consumer;

public final /* synthetic */ class _$$Lambda$ElmyraService$AV8onMO5IkvT88F5MAxNGAFWl18 implements Consumer {
    private final /* synthetic */ ElmyraService f$0;

    public /* synthetic */ _$$Lambda$ElmyraService$AV8onMO5IkvT88F5MAxNGAFWl18(ElmyraService elmyraService) {
        this.f$0 = elmyraService;
    }

    public final void accept(Object obj) {
        ((Action) obj).setListener(this.f$0.mActionListener);
    }
}
