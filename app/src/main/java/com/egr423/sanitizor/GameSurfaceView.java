package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The GameSurfaceView which creates a Game Thread and passes information to it
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread mGameThread;


    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Start the game
        mGameThread = new GameThread(holder, getContext());
        mGameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGameThread.stopThread();
    }

    public void changeAcceleration(float x, float y) {
        if (mGameThread != null) {
            mGameThread.changeAcceleration(x, y);
        }
    }

    //Called from GameActivity whenever a button is pushed
    public void buttonClicked() {
        mGameThread.buttonClicked();
    }

    public boolean getGameOver(){
        return mGameThread.getmSanitizorGame().getGameOver();
    }

    public int getPlayerScore(){
        return mGameThread.getPlayerScore();
    }

    public GameThread getmGameThread(){
        return mGameThread;
    }
}
