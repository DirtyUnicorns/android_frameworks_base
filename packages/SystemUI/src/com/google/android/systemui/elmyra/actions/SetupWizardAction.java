// package com.google.android.systemui.elmyra.actions;

// import android.content.Context;
// import android.content.Intent;
// import android.os.UserHandle;
// import android.os.UserManager;
// import com.android.keyguard.KeyguardUpdateMonitor;
// import com.android.keyguard.KeyguardUpdateMonitorCallback;
// import com.android.systemui.SysUiServiceProvider;
// import com.android.systemui.R;
// import com.android.systemui.statusbar.phone.StatusBar;
// import com.google.android.systemui.elmyra.gates.Gate;
// import com.google.android.systemui.elmyra.gates.Gate.Listener;
// import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
// import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
// import java.util.Collections;

// public class SetupWizardAction extends Action {
//     private boolean mDeviceInDemoMode;
//     private final KeyguardDeferredSetup mKeyguardDeferredSetupGate;
//     private final Listener mKeyguardDeferredSetupListener = new C15912();
//     private final LaunchOpa mLaunchOpa;
//     private final SettingsAction mSettingsAction;
//     private final String mSettingsPackageName;
//     private boolean mUserCompletedSuw;
//     private final KeyguardUpdateMonitorCallback mUserSwitchCallback = new C15901();

//     /* renamed from: com.google.android.systemui.elmyra.actions.SetupWizardAction$1 */
//     class C15901 extends KeyguardUpdateMonitorCallback {
//         C15901() {
//         }

//         public void onUserSwitching(int i) {
//             SetupWizardAction.this.mDeviceInDemoMode = UserManager.isDeviceInDemoMode(SetupWizardAction.this.getContext());
//             SetupWizardAction.this.notifyListener();
//         }
//     }

//     /* renamed from: com.google.android.systemui.elmyra.actions.SetupWizardAction$2 */
//     class C15912 implements Listener {
//         C15912() {
//         }

//         public void onGateChanged(Gate gate) {
//             SetupWizardAction.this.mUserCompletedSuw = ((KeyguardDeferredSetup) gate).isSuwComplete();
//             SetupWizardAction.this.notifyListener();
//         }
//     }

//     public SetupWizardAction(Context context, SettingsAction settingsAction, LaunchOpa launchOpa) {
//         super(context, null);
//         this.mSettingsPackageName = context.getResources().getString(R.string.settings_app_package_name);
//         this.mSettingsAction = settingsAction;
//         this.mLaunchOpa = launchOpa;
//         KeyguardUpdateMonitor.getInstance(context).registerCallback(this.mUserSwitchCallback);
//         this.mKeyguardDeferredSetupGate = new KeyguardDeferredSetup(context, Collections.emptyList());
//         this.mKeyguardDeferredSetupGate.activate();
//         this.mKeyguardDeferredSetupGate.setListener(this.mKeyguardDeferredSetupListener);
//         this.mUserCompletedSuw = this.mKeyguardDeferredSetupGate.isSuwComplete();
//     }

//     public boolean isAvailable() {
//         return (this.mDeviceInDemoMode || !this.mLaunchOpa.isAvailable() || this.mUserCompletedSuw || this.mSettingsAction.isAvailable()) ? false : true;
//     }

//     public void onProgress(float f, int i) {
//         updateFeedbackEffects(f, i);
//     }

//     public void onTrigger(DetectionProperties detectionProperties) {
//         ((StatusBar) SysUiServiceProvider.getComponent(getContext(), StatusBar.class)).cancelCurrentTouch();
//         triggerFeedbackEffects(detectionProperties);
//         if (!this.mUserCompletedSuw && !this.mSettingsAction.isAvailable()) {
//             Intent intent = new Intent();
//             intent.setAction("com.google.android.settings.ASSIST_GESTURE_TRAINING");
//             intent.setPackage(this.mSettingsPackageName);
//             intent.setFlags(268468224);
//             getContext().startActivityAsUser(intent, UserHandle.of(-2));
//         }
//     }

//     protected void triggerFeedbackEffects(DetectionProperties detectionProperties) {
//         super.triggerFeedbackEffects(detectionProperties);
//         this.mLaunchOpa.triggerFeedbackEffects(detectionProperties);
//     }

//     protected void updateFeedbackEffects(float f, int i) {
//         super.updateFeedbackEffects(f, i);
//         this.mLaunchOpa.updateFeedbackEffects(f, i);
//     }
// }
