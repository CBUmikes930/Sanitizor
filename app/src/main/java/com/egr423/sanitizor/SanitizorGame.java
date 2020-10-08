package com.egr423.sanitizor;

import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame {

    public static boolean PLAY_GAME_AUDIO = true;

    private Player mPlayer;


    private Context mContext;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private Paint mPaint;
    private boolean mGameOver;

    public SanitizorGame(Context context, int surfaceWidth, int surfaceHeight) {
        mContext = context;
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        //Create a player object
        mPlayer = new Player(mSurfaceWidth, mSurfaceHeight);

        //Start the game
        newGame();
    }

    public void newGame() {
        mGameOver = false;

        //Reset Player position
        mPlayer.setStartPosition();
    }

    public void update(PointF velocity) {
        if (mGameOver) return;

        //Move player
        mPlayer.move(velocity);

        //Check for win (for now, nothing)
        mGameOver = false;
    }

    public void draw(Canvas canvas) {
        //Clear canvas
        canvas.drawColor(mContext.getResources().getColor(R.color.bgColor));

        //Draw Player
        mPlayer.draw(canvas);
    }
}
