package com.traffy.attapon.traffybus.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by chanpc on 7/26/2016.
 */
public class Accelerometer implements SensorEventListener {

    float SHAKE_THRESHOLD = 0;
    private long mShakeTimestamp;
    private int mShakeCount;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    float x,y,z;
    public Accelerometer()
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public String getAccel()
    {
        String temp = String.valueOf(x)+" "+String.valueOf(y)+" "+String.valueOf(z);
        return temp;
    }
}
