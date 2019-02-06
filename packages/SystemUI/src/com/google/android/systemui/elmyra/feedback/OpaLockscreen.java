// package com.google.android.systemui.elmyra.feedback;

// import android.animation.Animator;
// import android.animation.AnimatorSet;
// import android.animation.ObjectAnimator;
// import android.animation.PropertyValuesHolder;
// import android.content.Context;
// import android.view.View;
// import android.view.animation.AccelerateInterpolator;
// import android.view.animation.DecelerateInterpolator;
// import android.view.animation.Interpolator;
// import com.android.systemui.Dependency;
// import com.android.systemui.SysUiServiceProvider;
// import com.android.systemui.R;
// import com.android.systemui.statusbar.phone.KeyguardBottomAreaView;
// import com.android.systemui.statusbar.phone.LockIcon;
// import com.android.systemui.statusbar.phone.StatusBar;
// import com.android.systemui.statusbar.policy.KeyguardMonitor;
// import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

// public class OpaLockscreen implements FeedbackEffect {
//     private static final Interpolator LOCK_ICON_HIDE_INTERPOLATOR = new DecelerateInterpolator();
//     private static final Interpolator LOCK_ICON_SHOW_INTERPOLATOR = new AccelerateInterpolator();
//     private final Context mContext;
//     private Animator mHideLockIconAnimator;
//     private KeyguardBottomAreaView mKeyguardBottomAreaView;
//     private final KeyguardMonitor mKeyguardMonitor = ((KeyguardMonitor) Dependency.get(KeyguardMonitor.class));
//     private LockIcon mLockIcon;
//     private FeedbackEffect mLockscreenOpaLayout;
//     private Animator mShowLockIconAnimator;

//     public OpaLockscreen(Context context) {
//         this.mContext = context;
//         refreshLockscreenOpaLayout();
//     }

//     private ObjectAnimator createAlphaObjectAnimator(View view, float f, int i, int i2, Interpolator interpolator) {
//         ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{f});
//         ofFloat.setDuration((long) i);
//         ofFloat.setStartDelay((long) i2);
//         ofFloat.setInterpolator(interpolator);
//         return ofFloat;
//     }

//     private Animator createHideAnimator(View view) {
//         Animator createScaleObjectAnimator = createScaleObjectAnimator(view, 0.0f, 200, 0, LOCK_ICON_HIDE_INTERPOLATOR);
//         Animator createAlphaObjectAnimator = createAlphaObjectAnimator(view, 0.0f, 200, 0, LOCK_ICON_HIDE_INTERPOLATOR);
//         Animator animatorSet = new AnimatorSet();
//         animatorSet.play(createScaleObjectAnimator).with(createAlphaObjectAnimator);
//         return animatorSet;
//     }

//     private ObjectAnimator createScaleObjectAnimator(View view, float f, int i, int i2, Interpolator interpolator) {
//         PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[2];
//         propertyValuesHolderArr[0] = PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{f});
//         propertyValuesHolderArr[1] = PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{f});
//         ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, propertyValuesHolderArr);
//         ofPropertyValuesHolder.setDuration((long) i);
//         ofPropertyValuesHolder.setStartDelay((long) i2);
//         ofPropertyValuesHolder.setInterpolator(interpolator);
//         return ofPropertyValuesHolder;
//     }

//     private Animator createShowAnimator(View view) {
//         Animator createScaleObjectAnimator = createScaleObjectAnimator(view, 1.0f, 200, 175, LOCK_ICON_SHOW_INTERPOLATOR);
//         Animator createAlphaObjectAnimator = createAlphaObjectAnimator(view, 1.0f, 200, 175, LOCK_ICON_SHOW_INTERPOLATOR);
//         Animator animatorSet = new AnimatorSet();
//         animatorSet.play(createScaleObjectAnimator).with(createAlphaObjectAnimator);
//         return animatorSet;
//     }

//     private void hideLockIcon() {
//         this.mShowLockIconAnimator.cancel();
//         if (!isLockIconHidden()) {
//             if (this.mLockIcon.isAttachedToWindow()) {
//                 this.mHideLockIconAnimator.start();
//                 return;
//             }
//             this.mLockIcon.setAlpha(0.0f);
//             this.mLockIcon.setScaleX(0.0f);
//             this.mLockIcon.setScaleY(0.0f);
//         }
//     }

//     private boolean isLockIconHidden() {
//         return this.mHideLockIconAnimator.isStarted() || (this.mLockIcon.getAlpha() == 0.0f && this.mLockIcon.getScaleX() == 0.0f && this.mLockIcon.getScaleY() == 0.0f);
//     }

//     private boolean isLockIconShown() {
//         return this.mShowLockIconAnimator.isStarted() || (this.mLockIcon.getAlpha() == 1.0f && this.mLockIcon.getScaleX() == 1.0f && this.mLockIcon.getScaleY() == 1.0f);
//     }

//     private void refreshLockscreenOpaLayout() {
//         StatusBar statusBar = (StatusBar) SysUiServiceProvider.getComponent(this.mContext, StatusBar.class);
//         if (statusBar == null || statusBar.getKeyguardBottomAreaView() == null || !this.mKeyguardMonitor.isShowing()) {
//             this.mKeyguardBottomAreaView = null;
//             this.mLockIcon = null;
//             this.mLockscreenOpaLayout = null;
//             return;
//         }
//         KeyguardBottomAreaView keyguardBottomAreaView = statusBar.getKeyguardBottomAreaView();
//         if (this.mLockscreenOpaLayout == null || !keyguardBottomAreaView.equals(this.mKeyguardBottomAreaView)) {
//             this.mKeyguardBottomAreaView = keyguardBottomAreaView;
//             if (this.mLockIcon != null) {
//                 showLockIcon();
//             }
//             this.mLockIcon = keyguardBottomAreaView.getLockIcon();
//             this.mHideLockIconAnimator = createHideAnimator(this.mLockIcon);
//             this.mShowLockIconAnimator = createShowAnimator(this.mLockIcon);
//             if (this.mLockscreenOpaLayout != null) {
//                 this.mLockscreenOpaLayout.onRelease();
//             }
//             this.mLockscreenOpaLayout = (FeedbackEffect) keyguardBottomAreaView.findViewById(R.id.lockscreen_opa);
//         }
//     }

//     private void showLockIcon() {
//         this.mHideLockIconAnimator.cancel();
//         if (!isLockIconShown()) {
//             if (this.mLockIcon.isAttachedToWindow()) {
//                 this.mShowLockIconAnimator.start();
//                 return;
//             }
//             this.mLockIcon.setAlpha(1.0f);
//             this.mLockIcon.setScaleX(1.0f);
//             this.mLockIcon.setScaleY(1.0f);
//         }
//     }

//     public void onProgress(float f, int i) {
//         refreshLockscreenOpaLayout();
//         if (this.mLockscreenOpaLayout != null) {
//             hideLockIcon();
//             this.mLockscreenOpaLayout.onProgress(f, i);
//         }
//     }

//     public void onRelease() {
//         refreshLockscreenOpaLayout();
//         if (this.mLockscreenOpaLayout != null) {
//             showLockIcon();
//             this.mLockscreenOpaLayout.onRelease();
//         }
//     }

//     public void onResolve(DetectionProperties detectionProperties) {
//         refreshLockscreenOpaLayout();
//         if (this.mLockscreenOpaLayout != null) {
//             showLockIcon();
//             this.mLockscreenOpaLayout.onResolve(detectionProperties);
//         }
//     }
// }
