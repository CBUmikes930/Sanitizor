package com.egr423.sanitizor;

import android.annotation.SuppressLint;
import android.graphics.PointF;
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
    private GameSurfaceView mSurfaceView;

    //Used in swipe and joystick controls
    private static final Joystick joystick = Joystick.getInstance();
    //Used in gyro controls
    private Sensor mAccelerometer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeID = (SettingsDialogue.useDarkMode) ? R.style.DarkTheme : R.style.LightTheme;
        setTheme(themeID);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        mSurfaceView = findViewById(R.id.gameSurface);

        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            //Initialize sensors
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else{
            //Initialize Touch settings
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            //Player touched screen
                            if (SettingsDialogue.controlScheme == R.id.joystick_controls) {
                                //Move player based on location of touch relative to joystick
                                movePlayerHelper(event.getX(), event.getY());
                            } else {
                                //draw the joystick graphic at touch location
                                joystick.setCenter(new PointF(event.getX(), event.getY()));
                                joystick.resetHandlePos();
                                joystick.setDrawGraphic(true);
                            }
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //Move player based on location of touch relative to joystick (initial)
                            movePlayerHelper(event.getX(), event.getY());

                            return true;
                        case MotionEvent.ACTION_UP:
                            //Stop player motion
                            mSurfaceView.changeAcceleration(0, 0);

                            if (SettingsDialogue.controlScheme == R.id.joystick_controls) {
                                //Reset handle to center of joystick
                                joystick.resetHandlePos();
                            } else {
                                //Undraw joystick
                                joystick.setDrawGraphic(false);
                            }
                    }
                    return false;
                }
            });

            /*
            Button testButton = findViewById(R.id.TEST);

            testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String test = "WACK";
                    Toast toast = Toast.makeText(v.getContext(), test, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            */
        }
    }

    /**
     * Helper method used by the touch listener in order to calculate player speed based on Joystick
     * @param x - the touch x coord
     * @param y - the touch y coord
     */
    private void movePlayerHelper(float x, float y) {
        //Move the handle in the joystick
        joystick.setHandleCenter(new PointF(x, y));

        //Calculate relative to the max travel distance the handle of the joystick is
        float percentX = joystick.getHandleCenter().x - joystick.getCenter().x;
        percentX /= joystick.getOutRadius() - joystick.getHandleRadius();
        //Multiply that percentage by the max accelerometer speed to keep speed consistent between modes
        mSurfaceView.changeAcceleration((float) (percentX * -9.8), y);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            mSensorManager.unregisterListener(this, mAccelerometer);
        }
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