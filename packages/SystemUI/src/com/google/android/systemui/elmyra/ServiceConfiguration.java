package com.google.android.systemui.elmyra;

import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.gates.Gate;
import com.google.android.systemui.elmyra.sensors.GestureSensor;
import java.util.Collections;
import java.util.List;

public interface ServiceConfiguration {
    List<Action> getActions();
    
    List<FeedbackEffect> getFeedbackEffects();

    List<Gate> getGates();

    GestureSensor getGestureSensor();
}
