// Is this thing necessary? We will understand it later.

// package com.google.android.systemui.elmyra.actions;

// import android.app.KeyguardManager;
// import android.content.Context;
// import android.os.Bundle;
// import android.provider.Settings.Secure;
// import com.android.systemui.Dependency;
// import com.android.systemui.SysUiServiceProvider;
// import com.android.systemui.assist.AssistManager;
// import com.android.systemui.statusbar.phone.StatusBar;
// import com.google.android.systemui.AssistManagerGoogle;
// import com.google.android.systemui.OpaEnabledListener;
// import com.google.android.systemui.elmyra.UserContentObserver;
// import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
// import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
// import java.util.List;

// public class LaunchOpa extends Action {
//     private final AssistManager mAssistManager = ((AssistManager) Dependency.get(AssistManager.class));
//     private boolean mIsGestureEnabled = isGestureEnabled();
//     private boolean mIsOpaEnabled;
//     private final KeyguardManager mKeyguardManager = ((KeyguardManager) getContext().getSystemService("keyguard"));
//     private final OpaEnabledListener mOpaEnabledListener = new C15891();
//     private final UserContentObserver mSettingsObserver = new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_enabled"), new _$$Lambda$LaunchOpa$Z0JaMSicRwfMwFAmiKhALeNwbbw(this));

//     /* renamed from: com.google.android.systemui.elmyra.actions.LaunchOpa$1 */
//     class C15891 implements OpaEnabledListener {
//         C15891() {
//         }

//         public void onOpaEnabledReceived(Context context, boolean z, boolean z2, boolean z3) {
//             boolean z4 = z && z2 && z3;
//             if (LaunchOpa.this.mIsOpaEnabled != z4) {
//                 LaunchOpa.this.mIsOpaEnabled = z4;
//                 LaunchOpa.this.notifyListener();
//             }
//         }
//     }

//     public LaunchOpa(Context context, List<FeedbackEffect> list) {
//         super(context, list);
//         ((AssistManagerGoogle) this.mAssistManager).addOpaEnabledListener(this.mOpaEnabledListener);
//     }

//     private boolean isGestureEnabled() {
//         return Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_enabled", 1, -2) != 0;
//     }

//     private void updateGestureEnabled() {
//         boolean isGestureEnabled = isGestureEnabled();
//         if (this.mIsGestureEnabled != isGestureEnabled) {
//             this.mIsGestureEnabled = isGestureEnabled;
//             notifyListener();
//         }
//     }

//     public boolean isAvailable() {
//         return this.mIsGestureEnabled && this.mIsOpaEnabled;
//     }

//     public void launchOpa() {
//         launchOpa(0);
//     }

//     public void launchOpa(long j) {
//         Bundle bundle = new Bundle();
//         bundle.putInt("triggered_by", this.mKeyguardManager.isKeyguardLocked() ? 14 : 13);
//         bundle.putLong("latency_id", j);
//         this.mAssistManager.startAssist(bundle);
//     }

//     public void onProgress(float f, int i) {
//         updateFeedbackEffects(f, i);
//     }

//     public void onTrigger(DetectionProperties detectionProperties) {
//         ((StatusBar) SysUiServiceProvider.getComponent(getContext(), StatusBar.class)).cancelCurrentTouch();
//         triggerFeedbackEffects(detectionProperties);
//         launchOpa(detectionProperties != null ? detectionProperties.getActionId() : 0);
//     }

//     public String toString() {
//         StringBuilder stringBuilder = new StringBuilder();
//         stringBuilder.append(super.toString());
//         stringBuilder.append(" [mIsGestureEnabled -> ");
//         stringBuilder.append(this.mIsGestureEnabled);
//         stringBuilder.append("; mIsOpaEnabled -> ");
//         stringBuilder.append(this.mIsOpaEnabled);
//         stringBuilder.append("]");
//         return stringBuilder.toString();
//     }
// }
