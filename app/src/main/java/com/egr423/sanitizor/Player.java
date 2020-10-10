package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The player object which is the class controlled by the player.
 */
public class Player {

    //Player position values
    private final int WIDTH = 150; //Change WIDTH to change overall image size
    private final int HEIGHT; //Calculated when image is loaded in order to preserve aspect ratio
    private final int BOTTOM_PADDING = 50; //Padding to keep image off of bottom

    //Speed multiplier for the movement speed based on accelerometer
    private final double SPEED = 1.5;

    //Used when drawing the default player (Circle if image can't be loaded)
    private Paint mPaint;

    //The location of the top left corner of the player
    private PointF mTopLeft;

    //The image resource for the player
    private Drawable mPlayerImage;
    //Screen Dimensions
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public Player(int surfaceWidth, int surfaceHeight, Context context) {
        //Load the image from the resources
        mPlayerImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.player, null);
        if (mPlayerImage != null) {
            //If image was loaded, then calculate it's relative height compared to the WIDTH (aspect ratio)
            HEIGHT = (int) (WIDTH * ((float) mPlayerImage.getIntrinsicHeight() / (float) mPlayerImage.getIntrinsicWidth()));
        } else {
            //Couldn't load, so post a message and set HEIGHT = WIDTH
            Log.d("Player Error", "Could not load mPlayerImage from resource: R.drawable.player");
            HEIGHT = WIDTH;
        }
        //Set Screen dimensions
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;

        //Set initial position
        setStartPosition();

        //Set color
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xffaaaaff);
    }

    public void setPosition(int x, int y) {
        mTopLeft.x = x;
        mTopLeft.y = y;
    }

    public void move(PointF velocity) {
        //Move center by velocity on x-axis, but anchor y
        mTopLeft.offset((float) (-velocity.x * SPEED), 0);
        //Log.d("VELOCITY CHECK", "Original Velocity: " + -velocity.x);
        //Log.d("VELOCITY CHECK", "Modified Velocity: " + -velocity.x * SPEED);

        //Check if still on screen
        if (mTopLeft.x + WIDTH > mSurfaceWidth) {
            //If too far right, then set it's right side to the edge of screen
            mTopLeft.x = mSurfaceWidth - WIDTH;
        } else if (mTopLeft.x < 0) {
            //If too far left, then set it's left side to the edge of the screen
            mTopLeft.x = 0;
        }
    }

    public void draw(Canvas canvas) {
        if (mPlayerImage != null) {
            //If we have a player image, then draw it
            mPlayerImage.setBounds((int) mTopLeft.x, (int) mTopLeft.y, (int) mTopLeft.x + WIDTH, (int) mTopLeft.y + HEIGHT);
            mPlayerImage.draw(canvas);
        } else {
            //If we don't have a player image, then draw a circle
            canvas.drawCircle(mTopLeft.x + (float) (WIDTH / 2), mTopLeft.y + (float) (WIDTH / 2), (float) WIDTH / 2, mPaint);
        }
    }

    public void setStartPosition() {
        //Set player image to the center of the screen and the bottom off the screen's bottom by the padding
        mTopLeft = new PointF( (float) (mSurfaceWidth - WIDTH) / 2, mSurfaceHeight - (BOTTOM_PADDING + HEIGHT));
    }
}
