package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import android.view.View;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.phone.NavigationBarView;
import java.util.Arrays;
import java.util.List;

public class SquishyNavigationButtons extends NavigationBarEffect {
    private final KeyguardViewMediator mKeyguardViewMediator;
    private final SquishyViewController mViewController;

    public SquishyNavigationButtons(Context context) {
        super(context);
        this.mViewController = new SquishyViewController(context);
        this.mKeyguardViewMediator = (KeyguardViewMediator) SysUiServiceProvider.getComponent(context, KeyguardViewMediator.class);
    }

    protected List<FeedbackEffect> findFeedbackEffects(NavigationBarView navigationBarView) {
        int i;
        this.mViewController.clearViews();
        List views = navigationBarView.getBackButton().getViews();
        for (i = 0; i < views.size(); i++) {
            this.mViewController.addLeftView((View) views.get(i));
        }
        views = navigationBarView.getRecentsButton().getViews();
        for (i = 0; i < views.size(); i++) {
            this.mViewController.addRightView((View) views.get(i));
        }
        return Arrays.asList(new FeedbackEffect[]{this.mViewController});
    }

    protected boolean isActiveFeedbackEffect(FeedbackEffect feedbackEffect) {
        return !this.mKeyguardViewMediator.isShowingAndNotOccluded();
    }

    protected boolean validateFeedbackEffects(List<FeedbackEffect> list) {
        return this.mViewController.isAttachedToWindow();
    }
}
