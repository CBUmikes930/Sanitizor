package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
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
    protected Bitmap[] mImage;
    //Which sprite to draw
    int mStatus;
    //Animation start time
    Rect bounds;
    long mStartTime;
    boolean mAnimationIsRunning = false;
    private boolean fromPlayer;
    //Sound FX
    private MediaPlayer splashSound;


    public Projectile(Context context, Character character){
        this(context);
        fromPlayer = character.equals(SanitizorGame.mPlayer);
    }

    public Projectile(Context context) {
        mImage = new Bitmap[6];
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
            mImage[i] = BitmapFactory.decodeResource(context.getResources(), id);
        }

        //calculate height based off of the aspect ratio of the first image
        if (mImage[0] != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage[0].getWidth() * SanitizorGame.PIXEL_MULTIPLIER),
                    (int) (mImage[0].getHeight() * SanitizorGame.PIXEL_MULTIPLIER));
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
        return mStatus >= mImage.length;
    }

    public void draw(Canvas canvas) {
        final float ANIMATION_SPEED = 50;

        //Calculate what frame of animation we should be on
        if (mAnimationIsRunning) {
            long elapsedTime = System.currentTimeMillis() - mStartTime;
            int oldStatus = mStatus;
            mStatus = (int) Math.floor(elapsedTime / ANIMATION_SPEED);
            //Recalculate bounds based on new sprite dimensions
            if (oldStatus != mStatus && mStatus < mImage.length) {
                int centerX = bounds.centerX();
                int newLeft = (int) (centerX - (mImage[mStatus].getWidth() * SanitizorGame.PIXEL_MULTIPLIER * 0.5));

                bounds.set(newLeft, bounds.top,
                        newLeft + (int) (mImage[mStatus].getWidth() * SanitizorGame.PIXEL_MULTIPLIER),
                        bounds.top + (int) (mImage[mStatus].getHeight() * SanitizorGame.PIXEL_MULTIPLIER));
            }
        }
        //If the animation has not finished, then render the frame
        if (mStatus < mImage.length) {
            Matrix matrix = new Matrix();
            //Map to the bounds coordinates
            matrix.setRectToRect(new RectF(0, 0, mImage[mStatus].getWidth(), mImage[mStatus].getHeight()),
                    new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom),
                    Matrix.ScaleToFit.FILL);
            canvas.drawBitmap(mImage[mStatus], matrix, null);
        }
    }
}
