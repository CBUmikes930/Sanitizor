package com.egr423.sanitizor;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The base GameActivity which handles Accelerometer input and passes that through to the surfaceView
 */
public class GameActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private GameSurfaceView mSurfaceView;
    private static final Joystick joystick = Joystick.getInstance();
    float mInitX;
    float mInitY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (SettingsDialogue.useGyroControls) {
            //Initialize sensors
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else{
            mSurfaceView = findViewById(R.id.gameSurface);

            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mInitX = event.getX();
                            mInitY = event.getY();
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            float x = event.getX() - mInitX;
                            float y = event.getY() - mInitY;
                            mSurfaceView.changeAcceleration(-x, y);
                            return true;
                        case MotionEvent.ACTION_UP:
                            mInitX = Integer.MAX_VALUE;
                            mInitY = Integer.MAX_VALUE;
                            mSurfaceView.changeAcceleration(0, 0);
                    }
                    return false;
                }
            });

            Button testButton = findViewById(R.id.TEST);

            testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String test = "WACK";
                    Toast toast = Toast.makeText(v.getContext(), test, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }

        //Set the surface view
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsDialogue.useGyroControls) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
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