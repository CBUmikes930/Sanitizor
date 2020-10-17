package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

// THIS IS A TEST COMMIT TO JESSE's CONTRIBUTION

/**
 * Created by Jesse Breslin on 10/9/2020
 * <p>
 * The Enemy class along with its subclasses
 * May separate the subclasses into different class files later //TODO
 */


//TODO Add enemy movement commands;
//TODO Add attack command to each subclass
//TODO Create graphical components for each enemy type

public abstract class Enemy extends Character {

    private double[] enemySpeeds;

    private boolean wrappingx = false;
    private boolean wrappingy = false;


    public Enemy(String color, Context context, Point location) {
        mImage = ResourcesCompat.getDrawable(context.getResources(), R.drawable.enemy_red, null);
        if (mImage != null) {
            bounds = new Rect(0, 0,
                    (int) (mImage.getIntrinsicWidth() * SanitizorGame.pixelMultiplier),
                    (int) (mImage.getIntrinsicHeight() * SanitizorGame.pixelMultiplier));
            bounds.offsetTo(location.x, location.y);
        } else {
            Log.d("Enemy Error", "Could not load mEnemyImage from resource: R.drawable.enemy_" + color);
        }
        SPEED = .5;
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

    private void wrapScreen() {

    }

    public void setPosition(int x, int y) {
        bounds.left = x;
        bounds.top = y;
    }


    public void draw(Canvas canvas) {
        if (mImage != null) {
            //If we have a player image, then draw it
            mImage.setBounds((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom);
            mImage.draw(canvas);

        }
    }
}



