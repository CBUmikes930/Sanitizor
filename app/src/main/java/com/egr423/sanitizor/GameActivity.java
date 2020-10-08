package com.egr423.sanitizor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The base GameActivity which handles Accelerometer input and passes that through to the surfaceView
 */
public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private GameSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Initialize sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Set the surface view
        mSurfaceView = findViewById(R.id.gameSurface);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Get Accelerometer values;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        //Move Player
        mSurfaceView.changeAcceleration(x, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}