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
public class EnemyProjectile extends Projectile {

    private Drawable mImage;
    public EnemyProjectile(Context context) {
        super(context);
        SPEED = -SPEED;
        mImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy_projectile, null);
        if (mImage != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage.getIntrinsicWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage.getIntrinsicHeight() * SanitizorGame.pixelMultiplier));
        }
    }


    public void move() {
        if (!mAnimationIsRunning) {
            //Move up a speed
            bounds.offset(0, -SPEED);
            //If collided with the top of screen, then play animation
            if (bounds.bottom <= 0) {
                bounds.offsetTo(bounds.left, 0);
                startAnimation();
            }
        }
    }

    public void draw(Canvas canvas){
        float ANIMATION_SPEED = 1;
        if (mAnimationIsRunning) {
            long elapsedTime = System.currentTimeMillis() - mStartTime;
            mStatus = (int) Math.floor(elapsedTime / ANIMATION_SPEED);
        }

        mImage.setBounds(bounds);
        mImage.draw(canvas);
    }


}
