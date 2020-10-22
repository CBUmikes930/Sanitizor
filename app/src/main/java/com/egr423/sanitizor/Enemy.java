package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

/**
 * Created by Jesse Breslin on 10/9/2020
 * <p>
 * The Enemy class along with its subclasses
 * May separate the subclasses into different class files later //TODO
 */


//TODO Add enemy movement commands;
//TODO Add attack command to each subclass
//TODO Create graphical components for each enemy type

public class Enemy extends Character {

    private double[] enemySpeeds;
    private Drawable[] mSprites = new Drawable[6];

    private int hitPoints;

    private int mDeathStatus;
    private boolean mDeathAnimationIsRunning = false;

    private boolean wrappingx = false;
    private boolean wrappingy = false;
    private long mDeathStartTime;


    public Enemy(String color, Context context, Point location) {
        mImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy, null);
        for (int i = 0; i < 6; i++) {
            //Get sprite name
            String name = "enemy_" + (i + 1);
            //Get sprite id
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            if (id == 0) {
                Log.e("Projectile Error", "ID lookup for resource " + name + " failed.");
            }
            //Get sprite
            mSprites[i] = ResourcesCompat.getDrawable(context.getResources(), id, null);
        }


        mDeathStatus = 0;
        if (mImage != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage.getIntrinsicWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage.getIntrinsicHeight() * SanitizorGame.pixelMultiplier));
            bounds.offsetTo(location.x, location.y);
            mSprites[0] = mImage;
        } else {
            Log.d("Enemy Error", "Could not load mEnemyImage from resource: R.drawable.enemy_" + color);
        }
        SPEED = .5;
        hitPoints = 2;
        shotCoolDown = 500;
        lastFired = 0;
    }


    public void move(PointF velocity) {
        bounds.offset((int) (velocity.x * SPEED), (int) (velocity.y * SPEED));

        if (bounds.right <= SanitizorGame.mSurfaceWidth && bounds.left > 0) {
            wrappingx = false;
        }
        if (bounds.bottom <= SanitizorGame.mSurfaceHeight && bounds.top > 0) {
            wrappingy = false;
        }

        if (!wrappingy && bounds.top > SanitizorGame.mSurfaceHeight) {
            bounds.offsetTo(bounds.left, -bounds.height());
            wrappingy = true;
        }

        if (!wrappingx) {
            if (bounds.left > SanitizorGame.mSurfaceWidth) {
                bounds.offsetTo(-bounds.width(), bounds.top);
                wrappingx = true;
            }
            if (bounds.left < -bounds.width()) {
                bounds.offsetTo(SanitizorGame.mSurfaceWidth, bounds.top);
                wrappingx = true;
            }
        }
    }


    public void hit() {
        hitPoints--;
        Log.d("Enemy", "Enemy hit, current HP: " + hitPoints);
    }

    public boolean shouldDestroy() {
        Log.d("Enemy", "Should Destroy " + (mDeathStatus >= mSprites.length));
        return mDeathStatus >= mSprites.length;
    }

    public void startDeathAnimation() {
        if (!mDeathAnimationIsRunning) {
            mDeathStartTime = System.currentTimeMillis();
            mDeathAnimationIsRunning = true;
            Log.d("Enemy", "Started Death Animation");
        }

    }

    public boolean isDeathAnimationRunning() {
        return mDeathAnimationIsRunning;
    }

    public boolean isDead() {
        return hitPoints <= 0;
    }

    public void draw(Canvas canvas) {
        if (mImage != null) {
            final float ANIMATION_SPEED = 50;
            //If we have a player image, then draw it

            if (mDeathAnimationIsRunning) {
                long elapsedTime = System.currentTimeMillis() - mDeathStartTime;
                mDeathStatus = (int) Math.floor(elapsedTime / ANIMATION_SPEED);
            }
            if (mDeathStatus < mSprites.length) {
                mSprites[mDeathStatus].setBounds(bounds);
                mSprites[mDeathStatus].draw(canvas);
            }
//            mImage.setBounds(bounds);
//            mImage.draw(canvas);
        }
    }
}



