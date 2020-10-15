package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import java.io.IOException;

/**
 * Created by Micah Steinbock on 10/13/2020
 *
 * Defines a projectile object that is fired in a straight line
 * When it collides with something it animates into a splashing animation
 */
public class Projectile {
    private final int WIDTH = 150; //Change WIDTH to change overall image size
    private final int HEIGHT; //Calculated when image is loaded in order to preserver aspect ratio

    private final float SPEED = 30;

    private PointF mTopLeft;

    //private Drawable mSprite;
    private Drawable[] mSprites = new Drawable[6];
    //Which sprite to draw
    private int mStatus;
    //Animation start time
    private long mStartTime;
    private boolean mAnimationIsRunning = false;
    //Sound FX
    private MediaPlayer splashSound;

    public Projectile(Context context) {
        //Start with the first sprite
        mStatus = 0;
        //Load in all the sprites into an array
        for (int i = 0; i < 6; i++) {
            //Get sprite name
            String name = "drop_0" + (i + 1);
            //Get sprite id
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (id == 0) {
                Log.e("Projectile Error", "ID lookup for resource " + name + " failed.");
            }
            //Get sprite
            mSprites[i] = ResourcesCompat.getDrawable(context.getResources(), id, null);
        }

        //calculate height based off of the aspect ratio of the first image
        if (mSprites[0] != null ) {
            HEIGHT = (int) (WIDTH * ((float) mSprites[0].getIntrinsicHeight() /
                    (float) mSprites[0].getIntrinsicWidth()));
        } else {
            HEIGHT = WIDTH;
        }

        //Load SoundFX
        splashSound = MediaPlayer.create(context, R.raw.splash);
    }

    public void setPosition(PointF topLeft) {
        mTopLeft = new PointF(topLeft.x, topLeft.y);
    }

    public void move() {
        //Move up a speed
        mTopLeft.offset(0, -SPEED);
        //If collided with the top of screen, then play animation
        if (mTopLeft.y <= 0) {
            mTopLeft.set(mTopLeft.x, 0);
            startAnimation();
        }
    }

    //TODO: Detect if collided with enemy
    public boolean didCollide(Object obj) {
        return false;
    }

    public void startAnimation() {
        //If it isn't already running, then start it
        if (!mAnimationIsRunning) {
            mStartTime = System.currentTimeMillis();
            mAnimationIsRunning = true;
            //TODO: Make sound play in sync
            splashSound.start();
        }
    }

    public void draw(Canvas canvas) {
        final float ANIMATION_SPEED = 50;

        //Calculate what frame of animation we should be on
        if (mAnimationIsRunning) {
            long elapsedTime = System.currentTimeMillis() - mStartTime;
            mStatus = (int) Math.floor(elapsedTime / ANIMATION_SPEED);
        }
        //If the animation has not finished, then render the frame
        if (mStatus < mSprites.length) {
            mSprites[mStatus].setBounds((int) mTopLeft.x, (int) mTopLeft.y,
                    (int) mTopLeft.x + WIDTH, (int) mTopLeft.y + HEIGHT);
            mSprites[mStatus].draw(canvas);
        }
    }
}
