package com.google.android.systemui.elmyra.sensors;

public interface Sensor {
    boolean isListening();

    void startListening();

    void stopListening();
}
