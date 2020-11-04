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

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Micah Steinbock on 10/13/2020
 * <p>
 * Defines a projectile object that is fired in a straight line
 * When it collides with something it animates into a splashing animation
 */
public class EnemyProjectile extends Projectile {
    public EnemyProjectile(Context context) {
        super(context);
        mImage = new Bitmap[1];
        SPEED = -SPEED;
        mImage[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy_projectile);
        if (mImage != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage[0].getWidth() * SanitizorGame.PIXEL_MULTIPLIER),
                    (int) (mImage[0].getHeight() * SanitizorGame.PIXEL_MULTIPLIER));
        }
        mLastMoved = System.currentTimeMillis();
    }

    public void move() {
        if (!mAnimationIsRunning) {
            //Move down a speed
            bounds.offset(0, (int) (-SPEED * (System.currentTimeMillis() - mLastMoved)));
            mLastMoved = System.currentTimeMillis();
        } else {
            mStatus++;
        }
    }

    public void draw(Canvas canvas){
        if (mStatus < mImage.length) {
            Matrix matrix = new Matrix();
            //Map to the bounds coordinates
            matrix.setRectToRect(new RectF(0, 0, mImage[mStatus].getWidth(), mImage[mStatus].getHeight()),
                    new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom),
                    Matrix.ScaleToFit.FILL);
            canvas.drawBitmap(mImage[0], matrix, null);
        }
    }
}
