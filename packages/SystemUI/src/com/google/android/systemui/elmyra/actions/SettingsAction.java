// package com.google.android.systemui.elmyra.actions;

// import android.content.Context;
// import com.android.systemui.SysUiServiceProvider;
// import com.android.systemui.R;
// import com.android.systemui.statusbar.phone.StatusBar;
// import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

// public class SettingsAction extends ServiceAction {
//     private final LaunchOpa mLaunchOpa;
//     private final String mSettingsPackageName;

//     public SettingsAction(Context context, LaunchOpa launchOpa) {
//         super(context, null);
//         this.mSettingsPackageName = context.getResources().getString(R.string.settings_app_package_name);
//         this.mLaunchOpa = launchOpa;
//     }

//     protected boolean checkSupportedCaller() {
//         return checkSupportedCaller(this.mSettingsPackageName);
//     }

//     public void onTrigger(DetectionProperties detectionProperties) {
//         ((StatusBar) SysUiServiceProvider.getComponent(getContext(), StatusBar.class)).cancelCurrentTouch();
//         super.onTrigger(detectionProperties);
//     }

//     protected void triggerAction() {
//         if (this.mLaunchOpa.isAvailable()) {
//             this.mLaunchOpa.launchOpa();
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
