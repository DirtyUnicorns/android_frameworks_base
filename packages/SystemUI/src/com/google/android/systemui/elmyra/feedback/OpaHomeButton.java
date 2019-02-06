// package com.google.android.systemui.elmyra.feedback;

// import android.content.Context;
// import android.view.View;
// import android.view.ViewParent;
// import com.android.systemui.SysUiServiceProvider;
// import com.android.systemui.keyguard.KeyguardViewMediator;
// import com.android.systemui.statusbar.phone.NavigationBarView;
// import java.util.ArrayList;
// import java.util.List;

// public class OpaHomeButton extends NavigationBarEffect {
//     private final KeyguardViewMediator mKeyguardViewMediator;
//     private NavigationBarView mNavigationBar;

//     public OpaHomeButton(Context context) {
//         super(context);
//         this.mKeyguardViewMediator = (KeyguardViewMediator) SysUiServiceProvider.getComponent(context, KeyguardViewMediator.class);
//     }

//     protected List<FeedbackEffect> findFeedbackEffects(NavigationBarView navigationBarView) {
//         List<FeedbackEffect> arrayList = new ArrayList();
//         List views = navigationBarView.getHomeButton().getViews();
//         int i = 0;
//         while (true) {
//             int i2 = i;
//             if (i2 < views.size()) {
//                 View view = (View) views.get(i2);
//                 if (view instanceof FeedbackEffect) {
//                     arrayList.add((FeedbackEffect) view);
//                 }
//                 i = i2 + 1;
//             } else {
//                 this.mNavigationBar = navigationBarView;
//                 return arrayList;
//             }
//         }
//     }

//     protected boolean isActiveFeedbackEffect(FeedbackEffect feedbackEffect) {
//         if (this.mKeyguardViewMediator.isShowingAndNotOccluded()) {
//             return false;
//         }
//         View currentView = this.mNavigationBar.getCurrentView();
//         for (ViewParent parent = ((View) feedbackEffect).getParent(); parent != null; parent = parent.getParent()) {
//             if (parent.equals(currentView)) {
//                 return true;
//             }
//         }
//         return false;
//     }

//     protected boolean validateFeedbackEffects(List<FeedbackEffect> list) {
//         for (int i = 0; i < list.size(); i++) {
//             if (!((View) list.get(i)).isAttachedToWindow()) {
//                 return false;
//             }
//         }
//         return true;
//     }
// }
