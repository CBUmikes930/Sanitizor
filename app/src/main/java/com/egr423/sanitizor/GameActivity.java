package com.egr423.sanitizor;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Micah Steinbock on 10/6/2020
 * <p>
 * The base GameActivity which handles Accelerometer input and passes that through to the surfaceView
 */
public class GameActivity extends AppCompatActivity implements SensorEventListener {

    //Used in swipe and joystick controls
    private static final Joystick joystick = Joystick.getInstance();
    private SensorManager mSensorManager;
    private GameSurfaceView mSurfaceView;
    //Used in gyro controls
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int themeID = (SettingsDialogue.useDarkMode) ? R.style.DarkTheme : R.style.LightTheme;
        setTheme(themeID);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        mSurfaceView = findViewById(R.id.gameSurface);

        setControlScheme();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setControlScheme() {
        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            //Initialize sensors for motion control
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            //Set tap listener to fire projectile
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mSurfaceView.buttonClicked();
                        return true;
                    }
                    return false;
                }
            });

            Button fireButton = findViewById(R.id.FireButton);
            fireButton.setVisibility(View.GONE);
        } else {
            //Initialize Touch settings for motion control
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
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

            //Set up button to fire projectile
            Button fireButton = findViewById(R.id.FireButton);
            fireButton.setVisibility(View.VISIBLE);
            fireButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSurfaceView.buttonClicked();
                }
            });
        }
    }

    private void resetControlScheme() {
        if (SettingsDialogue.controlScheme != R.id.gyro_controls) {
            try {
                mSensorManager.unregisterListener(this, mAccelerometer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (SettingsDialogue.controlScheme == R.id.joystick_controls) {
            mSurfaceView.resetJoystick();
        }
        setControlScheme();
    }

    /**
     * Helper method used by the touch listener in order to calculate player speed based on Joystick
     *
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
    protected void onStart() {
        super.onStart();

        SoundManager soundManager = SoundManager.getInstance();
        //soundManager.stopAll();
        soundManager.playSound("InGame_Final.ogg", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SoundManager.getInstance().resume();

        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SoundManager.getInstance().pause();

        if (SettingsDialogue.controlScheme == R.id.gyro_controls) {
            mSensorManager.unregisterListener(this, mAccelerometer);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SoundManager.getInstance().stopSound("InGame_Final.ogg");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Get Accelerometer values;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];

        //Move Player
        mSurfaceView.changeAcceleration(x, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void pauseGame(View view) {
        Log.d("GamePause", "Game Paused");
        mSurfaceView.pauseGame();
        SoundManager.getInstance().pause();
        SettingsDialogue settingsDialogue = new SettingsDialogue();
        settingsDialogue.show(getSupportFragmentManager(), "settingsFragment");
        settingsDialogue.setActivity(this);
        settingsDialogue.hideThemeOption(true);
    }

    public void resumeGame() {
        resetControlScheme();
        SoundManager.getInstance().resume();
        mSurfaceView.resumeGame();
    }

    public void killPlayer() {
        mSurfaceView.getmGameThread().getmSanitizorGame().killPlayer();
    }
}