package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Micah Steinbock on 10/6/2020
 *
 * The player object which is the class controlled by the player.
 */
public class Player extends Character {

    //Player position values
    private final int BOTTOM_PADDING = 50; //Padding to keep image off of bottom

    //Speed multiplier for the movement speed based on accelerometer


    //The location of the top left corner of the player

    //The image resource for the player
    private Drawable mImage;
    private boolean isAlive;
    //Screen Dimensions

    public Player(Context context) {
        //Load the image from the resources
        mImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.player, null);
        if (mImage != null) {
            //If image was loaded, then calculate it's relative height compared to the WIDTH (aspect ratio)
            bounds = new Rect(0, 0,
                    (int) (mImage.getIntrinsicWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage.getIntrinsicHeight() * SanitizorGame.pixelMultiplier));
        } else {
            //Couldn't load, so post a message and set HEIGHT = WIDTH
            Log.d("Player Error", "Could not load mPlayerImage from resource: R.drawable.player");
        }

        SPEED = 1.5;

        //Set initial position
        setStartPosition();

        //Set color
    }

    public void move(PointF velocity) {
        //Move center by velocity on x-axis, but anchor y
        bounds.offset((int) (-velocity.x * SPEED), 0);
        //Log.d("VELOCITY CHECK", "Original Velocity: " + -velocity.x);
        //Log.d("VELOCITY CHECK", "Modified Velocity: " + -velocity.x * SPEED);

        //Check if still on screen
        if (bounds.right > SanitizorGame.mSurfaceWidth) {
            //If too far right, then set it's right side to the edge of screen
            bounds.offsetTo(SanitizorGame.mSurfaceWidth - bounds.width(), bounds.top);
        } else if (bounds.left < 0) {
            //If too far left, then set it's left side to the edge of the screen
            bounds.offsetTo(0, bounds.top);
        }
    }

    public void draw(Canvas canvas) {
        if (mImage != null) {
            //If we have a player image, then draw it
            mImage.setBounds(bounds);
            mImage.draw(canvas);
        }
    }

    public void setStartPosition() {
        //Set player image to the center of the screen and the bottom off the screen's bottom by the padding
        bounds.offsetTo((SanitizorGame.mSurfaceWidth - bounds.width()) / 2,
                SanitizorGame.mSurfaceHeight - (BOTTOM_PADDING + bounds.height()));
    }
}
