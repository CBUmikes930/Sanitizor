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
    private Sensor mAccelerometer;
    private GameSurfaceView mSurfaceView;
    private static final Joystick joystick = Joystick.getInstance();
    float mInitX;
    float mInitY;

    //Temp variable to be replaced by setting at some point
    private final boolean IS_USING_JOYSTICK = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mSurfaceView = findViewById(R.id.gameSurface);

        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            //Initialize sensors
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else{
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            if (SettingsDialogue.controlScheme == R.id.joystick_controls) {
                                //Move the handle in the joystick
                                joystick.setHandleCenter(new PointF(event.getX(), event.getY()));

                                //Calculate relative to the max travel distance the handle of the joystick is
                                float percentX = joystick.getHandleCenter().x - joystick.getCenter().x;
                                percentX /= joystick.getOutRadius() - joystick.getHandleRadius();
                                //Multiply that percentage by the max accelerometer speed to keep speed consistent between modes
                                mSurfaceView.changeAcceleration((float) (percentX * -9.8), event.getY());
                            } else {
                                //Swipe input
                                mInitX = event.getX();
                                mInitY = event.getY();
                            }
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            if (SettingsDialogue.controlScheme == R.id.joystick_controls) {
                                //Move the handle in the joystick
                                joystick.setHandleCenter(new PointF(event.getX(), event.getY()));

                                //Calculate relative to the max travel distance the handle of the joystick is
                                float percentX = joystick.getHandleCenter().x - joystick.getCenter().x;
                                percentX /= joystick.getOutRadius() - joystick.getHandleRadius();
                                //Multiply that percentage by the max accelerometer speed to keep speed consistent between modes
                                mSurfaceView.changeAcceleration((float) (percentX * -9.8), event.getY());
                            } else {
                                mSurfaceView.changeAcceleration(-(event.getX() - mInitX), event.getY() - mInitY);
                            }
                            return true;
                        case MotionEvent.ACTION_UP:
                            //Stop player motion
                            mSurfaceView.changeAcceleration(0, 0);

                            if (SettingsDialogue.controlScheme == R.id.joystick_controls) {
                                //Reset handle to center of joystick
                                joystick.resetHandlePos();
                            } else {
                                mInitX = Integer.MAX_VALUE;
                                mInitY = Integer.MAX_VALUE;
                            }
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