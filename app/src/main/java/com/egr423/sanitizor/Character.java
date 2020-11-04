package com.egr423.sanitizor;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public abstract class Character {

    Rect bounds;
    Bitmap[] mImage;
    double SPEED;
    long lastFired;
    int shotCoolDown;

    public Rect getRect() {
        return bounds;
    }

    Point getPosition() {
        return new Point(bounds.left, bounds.top);
    }
}



