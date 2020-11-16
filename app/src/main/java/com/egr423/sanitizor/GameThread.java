package com.egr423.sanitizor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.SurfaceHolder;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * A GameThread which is responsible for starting and stopping the game through the SanitizorGame
 */
public class GameThread extends Thread {

    private SurfaceHolder mSurfaceHolder;
    private SanitizorGame mSanitizorGame;
    private boolean mThreadRunning;
    private boolean mIsPaused;
    private PointF mVelocity;

    public GameThread(SurfaceHolder holder, Context context) {
        mSurfaceHolder = holder;
        mThreadRunning = true;
        mIsPaused = false;

        mVelocity = new PointF();

        //Create a new game with boundaries determined by SurfaceView
        Canvas canvas = mSurfaceHolder.lockCanvas();
        mSanitizorGame = new SanitizorGame(context, canvas.getWidth(), canvas.getHeight());
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void run() {
        try {
            while (!getmSanitizorGame().getGameOver() && mThreadRunning) {
                if (!mIsPaused) {
                    Canvas canvas = mSurfaceHolder.lockCanvas();
                    mSanitizorGame.update(mVelocity);
                    mSanitizorGame.draw(canvas);
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                    SanitizorGame.setPauseStart(System.currentTimeMillis());
                    SanitizorGame.setPauseEnd(System.currentTimeMillis());
                }
            }
        } catch (NullPointerException ex) {
            //Canvas is destroyed while thread is running
            ex.printStackTrace();
        }
    }

    public void changeAcceleration(float xForce, float yForce) {
        mVelocity.x = xForce;
        mVelocity.y = yForce;
    }

    //Called from GameSurfaceView when a button is clicked
    public void buttonClicked() {
        mSanitizorGame.buttonClicked();
    }

    public void startThread() {
        mThreadRunning = true;
    }

    public void stopThread() {
        mThreadRunning = false;
    }

    public int getPlayerScore(){
        return mSanitizorGame.getPlayerScore();
    }

    public SanitizorGame getmSanitizorGame(){
        return mSanitizorGame;
    }

    public void pauseGame() {
        SanitizorGame.setPauseStart(System.currentTimeMillis());
        mIsPaused = true;
    }

    public void resumeGame() {
        SanitizorGame.setPauseEnd(System.currentTimeMillis());
        mIsPaused = false;
    }

    public void resetJoystick() {
        mSanitizorGame.resetJoystick();
    }
}