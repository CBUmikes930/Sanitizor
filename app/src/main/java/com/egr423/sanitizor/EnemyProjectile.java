package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Micah Steinbock on 10/13/2020
 * <p>
 * Defines a projectile object that is fired in a straight line
 * When it collides with something it animates into a splashing animation
 */
public class EnemyProjectile extends Projectile {

    private Bitmap[] mImage = new Bitmap[1];
    public EnemyProjectile(Context context) {
        super(context);
        SPEED = -SPEED;
        mImage[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_projectile);
        if (mImage != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage[0].getWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage[0].getHeight() * SanitizorGame.pixelMultiplier));
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

        Matrix matrix = new Matrix();
        //Set the destination rectangle
        RectF dst = new RectF(bounds.left,
                bounds.top,
                (float) (bounds.left + (bounds.width() * SanitizorGame.pixelMultiplier)),
                (float) (bounds.top + (bounds.height() * SanitizorGame.pixelMultiplier)));
        //Map to the bounds coordinates
        matrix.setRectToRect(new RectF(0, 0, bounds.width(), bounds.height()),
                dst,
                Matrix.ScaleToFit.FILL);
        //Rotate
        //matrix.postRotate(rotation, bounds.centerX(), bounds.centerY());
        canvas.drawBitmap(mImage[0], matrix, null);
    }


}
