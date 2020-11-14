package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;

public class PowerUp extends Projectile {
    float SPEED = -1f;
    protected Bitmap[] mImage;
    int mStatus;
    //Animation start time
    Rect bounds;
    long mStartTime;
    boolean mAnimationIsRunning = false;
    long mLastMoved;
    boolean mShouldDestroy = false;
    //Sound FX
    private MediaPlayer collectSound;


    public PowerUp(Context context) {
        super(context);
        mImage = new Bitmap[17];
        //Start with the first sprite
        mStatus = 0;
        //Load in all the sprites into an array
        for (int i = 0; i < mImage.length; i++) {
            //Get sprite name
            String name = "powerup_rapid_" + (i + 1);
            //Get sprite id
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (id == 0) {
                Log.e("Power up Error", "ID lookup for resource " + name + " failed.");
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
        //collectSound = MediaPlayer.create(context, R.raw.collect);
        mLastMoved = System.currentTimeMillis();
    }

    @Override
    public void setPosition(Point location) {
        bounds.offsetTo(location.x, location.y);
        Log.d("PowerUp", "Set position to: " + location.x + ", " + location.y);
    }

    @Override
    public void move(){
        Log.d("powerUp", "Current location:" + bounds.left +','+bounds.top);
        bounds.offset(0, (int)(SPEED * (mLastMoved - System.currentTimeMillis())));
        mLastMoved = System.currentTimeMillis();
        mShouldDestroy = bounds.top >= SanitizorGame.mSurfaceHeight;
    }

    @Override
    public void startAnimation(){
        if(!mAnimationIsRunning){
            mStartTime=System.currentTimeMillis();
            mAnimationIsRunning = true;
        }
    }

    @Override
    public boolean shouldDestroy(){
        Log.d("PowerUp: shouldDestroy", ""+ (bounds.top > SanitizorGame.mSurfaceHeight));
        return (mShouldDestroy);
    }

    @Override
    public Rect getRect() {
        return bounds;
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
        // Reset the animation if it has finished.
        if(mStatus >= mImage.length){
             mStatus = 0;
             mStartTime = System.currentTimeMillis();
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

    public void destroyPowerUp(){
        mShouldDestroy = true;
    }

    public void upgradePlayer(Player player){
        player.activateRapidFire();
        SoundManager.getInstance().playSound("Powerup.ogg");
    }
}
