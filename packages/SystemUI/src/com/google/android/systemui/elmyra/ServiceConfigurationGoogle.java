package com.google.android.systemui.elmyra;

import android.content.Context;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.actions.DismissTimer;
// import com.google.android.systemui.elmyra.actions.LaunchOpa;
// import com.google.android.systemui.elmyra.actions.SettingsAction;
// import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.SilenceCall;
import com.google.android.systemui.elmyra.actions.SnoozeAlarm;
import com.google.android.systemui.elmyra.actions.UnpinNotifications;
import com.google.android.systemui.elmyra.actions.Flashlight;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.feedback.HapticClick;
import com.google.android.systemui.elmyra.feedback.NavUndimEffect;
// import com.google.android.systemui.elmyra.feedback.OpaHomeButton;
// import com.google.android.systemui.elmyra.feedback.OpaLockscreen;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import com.google.android.systemui.elmyra.feedback.UserActivity;
import com.google.android.systemui.elmyra.gates.CameraVisibility;
import com.google.android.systemui.elmyra.gates.ChargingState;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.gates.KeyguardDeferredSetup;
import com.google.android.systemui.elmyra.gates.KeyguardProximity;
import com.google.android.systemui.elmyra.gates.NavigationBarVisibility;
import com.google.android.systemui.elmyra.gates.PowerSaveState;
import com.google.android.systemui.elmyra.gates.SetupWizard;
import com.google.android.systemui.elmyra.gates.SystemKeyPress;
import com.google.android.systemui.elmyra.gates.TelephonyActivity;
import com.google.android.systemui.elmyra.gates.UsbState;
import com.google.android.systemui.elmyra.gates.VrMode;
import com.google.android.systemui.elmyra.gates.WakeMode;
import com.google.android.systemui.elmyra.sensors.CHREGestureSensor;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import com.google.android.systemui.elmyra.sensors.JNIGestureSensor;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.ScreenStateAdjustment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceConfigurationGoogle implements ServiceConfiguration {
    private final List<Action> mActions = new ArrayList();
    private final Context mContext;
    private final List<FeedbackEffect> mFeedbackEffects;
    private final List<Gate> mGates;
    private final GestureSensor mGestureSensor;

    public ServiceConfigurationGoogle(Context context) {
        this.mContext = context;
        // OpaHomeButton opaHomeButton = new OpaHomeButton(context);
        // LaunchOpa launchOpa = new LaunchOpa(context, Arrays.asList(new FeedbackEffect[]{opaHomeButton, new OpaLockscreen(context)}));
        List asList = Arrays.asList(new Action[]{new Flashlight(context), new DismissTimer(context), new SnoozeAlarm(context), new SilenceCall(context), /*new SettingsAction(context, launchOpa)*/});
        // CameraAction cameraAction = new CameraAction(context, Arrays.asList(new FeedbackEffect[]{opaHomeButton}));
        this.mActions.addAll(asList);
        this.mActions.add(new UnpinNotifications(context));
        // this.mActions.add(cameraAction);
        // this.mActions.add(new SetupWizardAction(context, r2, launchOpa));
        // this.mActions.add(launchOpa);
        this.mFeedbackEffects = new ArrayList();
        this.mFeedbackEffects.add(new HapticClick(context));
        this.mFeedbackEffects.add(new SquishyNavigationButtons(context));
        this.mFeedbackEffects.add(new NavUndimEffect(context));
        this.mFeedbackEffects.add(new UserActivity(context));
        this.mGates = new ArrayList();
        this.mGates.add(new WakeMode(context));
        this.mGates.add(new ChargingState(context));
        this.mGates.add(new UsbState(context));
        this.mGates.add(new KeyguardProximity(context));
        //this.mGates.add(new SetupWizard(context, Arrays.asList(new Action[]{r2})));
        this.mGates.add(new NavigationBarVisibility(context, asList));
        this.mGates.add(new SystemKeyPress(context));
        this.mGates.add(new TelephonyActivity(context));
        this.mGates.add(new VrMode(context));
        this.mGates.add(new KeyguardDeferredSetup(context, asList));
        // this.mGates.add(new CameraVisibility(context, cameraAction));
        this.mGates.add(new PowerSaveState(context));
        List arrayList = new ArrayList();
        arrayList.add(new ScreenStateAdjustment(context));
        GestureConfiguration gestureConfiguration = new GestureConfiguration(context, arrayList);
        if (JNIGestureSensor.isAvailable(context)) {
            this.mGestureSensor = new JNIGestureSensor(context, gestureConfiguration);
        } else {
            this.mGestureSensor = new CHREGestureSensor(context, gestureConfiguration, new SnapshotConfiguration(context));
        }
    }

    @Override
    public List<Action> getActions() {
        return this.mActions;
    }

    @Override
    public List<FeedbackEffect> getFeedbackEffects() {
        return this.mFeedbackEffects;
    }

    @Override
    public List<Gate> getGates() {
        return this.mGates;
    }

    @Override
    public GestureSensor getGestureSensor() {
        return this.mGestureSensor;
    }
}
