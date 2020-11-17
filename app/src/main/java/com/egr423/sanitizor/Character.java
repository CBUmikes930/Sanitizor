package com.egr423.sanitizor;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public abstract class Character {

    Rect bounds;
    Bitmap[] mImage;
    double SPEED = 0.2;
    long lastFired;
    int shotCoolDown = 5000;

    public Rect getRect() {
        return bounds;
    }

    Point getPosition() {
        return new Point(bounds.left, bounds.top);
    }
}



