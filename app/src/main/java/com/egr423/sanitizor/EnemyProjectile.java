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

    public EnemyProjectile(Context context) {
        super(context);
        SPEED = -SPEED;

    }


}
