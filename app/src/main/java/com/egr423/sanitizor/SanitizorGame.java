package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import androidx.core.content.ContextCompat;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame{

    private Player mPlayer;
    private Joystick joystick = Joystick.getInstance();

    //Context is saved in order to load resources
    private Context mContext;
    //Screen dimensions
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    
    private boolean mGameOver;

    public SanitizorGame(Context context, int surfaceWidth, int surfaceHeight) {
        mContext = context;
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        //Create a player object
        mPlayer = new Player(mSurfaceWidth, mSurfaceHeight, context);

        joystick.setCenter((float) mSurfaceWidth / 2, (float) mSurfaceHeight - 150);
        joystick.setOuterColor(context.getResources().getColor(R.color.joystick_bg));
        joystick.setInnerColor(context.getResources().getColor(R.color.joystick_fg));

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

        //Draw the joystick circle
        joystick.draw(canvas);
    }
}
