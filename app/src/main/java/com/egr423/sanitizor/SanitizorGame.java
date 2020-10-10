package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The Game handler which controls object creation and calculations
 */
public class SanitizorGame{

    private Player mPlayer;
    private Joystick joystick = Joystick.getInstance();
    private Enemy[] enemies;

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
        mPlayer = new Player(mSurfaceWidth, mSurfaceHeight, mContext);

        //Create enemy objects
//        for(int i = 0; i < 10; i++){
//            PointF location = new PointF(0,0); // TODO figure out spawn grid equation for enemies
//            enemies[i] = new Enemy().enemy(context, surfaceWidth,surfaceHeight, location);
//        }

        //Set joystick position to bottom center of screen and set handle to center
        joystick.setCenter(new PointF((float) mSurfaceWidth / 2, (float) mSurfaceHeight - 150));
        joystick.resetHandlePos();
        joystick.setOuterColor(context.getResources().getColor(R.color.joystick_bg));
        joystick.setHandleColor(context.getResources().getColor(R.color.joystick_fg));

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
        //Clear Canvas
        TypedValue a = new TypedValue();
        mContext.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        canvas.drawColor(a.data);

        //Draw Player
        mPlayer.draw(canvas);

        //Draw Enemies
        //for (Enemy e: enemies) {
        //    e.draw(canvas);
        //}

        //Draw the joystick circle
        joystick.draw(canvas);


    }
}
