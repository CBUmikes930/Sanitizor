package com.egr423.sanitizor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The player object which is the class controlled by the player.
 */
public class Player {

    //Temp Variable to make player a circle until graphics are made
    public final int RADIUS = 100;
    public final int BOTTOM_PADDING = 50;

    private Paint mPaint;
    //The location of the center of the player
    private PointF mCenter;

    //Screen Dimensions
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public Player(int surfaceWidth, int surfaceHeight) {
        //Set Screen dimensions
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        //Set initial position
        setStartPosition();

        //Set color
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffaaaaff);
    }

    public void setCenter(int x, int y) {
        mCenter.x = x;
        mCenter.y = y;
    }

    public void move(PointF velocity) {
        //Move center by velocity on x-axis, but anchor y
        mCenter.offset(-velocity.x, 0);

        //Check if still on screen
        if (mCenter.x > mSurfaceWidth - RADIUS) {
            //If too far right, then set it's right side to the edge of screen
            mCenter.x = mSurfaceWidth - RADIUS;
        } else if (mCenter.x < RADIUS) {
            //If too far left, then set it's left side to the edge of the screen
            mCenter.x = RADIUS;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenter.x, mCenter.y, RADIUS, mPaint);
    }

    public void setStartPosition() {
        mCenter = new PointF( (float) mSurfaceWidth / 2, mSurfaceHeight - (BOTTOM_PADDING + RADIUS));
    }
}
