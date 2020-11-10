package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;

import androidx.annotation.NonNull;

public class BlueEnemy extends Enemy {
    public BlueEnemy(@NonNull Context context, Point location) {
        super(ENEMY_COLORS.BLUE, context, location, 6);
        ATTACK_PHASE = 8000;
    }

    @Override
    public void attack(@NonNull PointF velocity) {
        wrapGrid();
//        checkAtOriginalPos();
        bounds.offset(0, (int) (velocity.y * SPEED));
        wrapScreen();

        // NEW ATTACK
        int x = SanitizorGame.mPlayer.bounds.left - bounds.left;
        int y = SanitizorGame.mPlayer.bounds.top - bounds.top;
        double direction = Math.atan((0.0 + y) / x);
        int sign = 1;
        if (x < 0) {
            sign = -1;
        }
        int speed = 40;
        bounds.offset((int) ((speed * Math.cos(direction) * sign)),
                (int) (velocity.y*SPEED));
        wrapScreen();
    }
}
