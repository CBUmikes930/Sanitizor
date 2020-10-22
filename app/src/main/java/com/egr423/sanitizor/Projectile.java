package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Micah Steinbock on 10/13/2020
 * <p>
 * Defines a projectile object that is fired in a straight line
 * When it collides with something it animates into a splashing animation
 */
public class Projectile {
    int SPEED = 30;

    //private Drawable mSprite;
    private Drawable[] mSprites = new Drawable[6];
    //Which sprite to draw
    private int mStatus;
    //Animation start time
    Rect bounds;
    private long mStartTime;

    private boolean mAnimationIsRunning = false;
    private boolean fromPlayer;
    //Sound FX
    private MediaPlayer splashSound;


    public Projectile(Context context, Character character){
        this(context);
        fromPlayer = character.equals(SanitizorGame.mPlayer);
    }

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
        if (mSprites[0] != null) {
            bounds = new Rect(0, 0,
                    (int) (mSprites[0].getIntrinsicWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mSprites[0].getIntrinsicHeight() * SanitizorGame.pixelMultiplier));

            Drawable sprite = mSprites[0];

            sprite.getHotspotBounds();

        }
        //Load SoundFX
        //splashSound = MediaPlayer.create(context, R.raw.splash);
    }

    public void setPosition(Point location) {
       bounds.offsetTo(location.x, location.y);
    }

    public void move() {
        if (!mAnimationIsRunning) {
            //Move up a speed
            bounds.offset(0, -SPEED);
            //If collided with the top of screen, then play animation
            if (bounds.top <= 0) {
                bounds.offsetTo(bounds.left, 0);
                startAnimation();
            }
        }
    }

    public Rect getRect() {
        return bounds;
    }

    public void startAnimation() {
        //If it isn't already running, then start it
        if (!mAnimationIsRunning) {
            mStartTime = System.currentTimeMillis();
            mAnimationIsRunning = true;
            //TODO: Make sound play in sync
            //splashSound.start();
        }
    }


    public boolean isAnimationRunning() {
        return mAnimationIsRunning;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    public boolean shouldDestroy() {
        return mStatus >= mSprites.length;
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
            mSprites[mStatus].setBounds(bounds);
            mSprites[mStatus].draw(canvas);
        }
    }
}
