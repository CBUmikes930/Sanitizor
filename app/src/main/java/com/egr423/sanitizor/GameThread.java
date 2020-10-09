package com.egr423.sanitizor;

import android.content.Context;
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
    private PointF mVelocity;

    public GameThread(SurfaceHolder holder, Context context) {
        mSurfaceHolder = holder;
        mThreadRunning = true;

        mVelocity = new PointF();

        //Create a new game with boundaries determined by SurfaceView
        Canvas canvas = mSurfaceHolder.lockCanvas();
        mSanitizorGame = new SanitizorGame(context, canvas.getWidth(), canvas.getHeight());
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void run() {
        try {
            while (mThreadRunning) {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                mSanitizorGame.update(mVelocity);
                mSanitizorGame.draw(canvas);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        } catch (NullPointerException ex) {
            //Canvas is destroyed while thread is running
            ex.printStackTrace();
        }
    }

    public void changeAcceleration(float xForce, float yForce) {
        mVelocity.x = xForce;
        mVelocity.y = yForce;
        System.out.println("xForce: " + xForce);
        //System.out.println("yForce: " + yForce);
    }

    public void stopThread() {
        mThreadRunning = false;
    }
}