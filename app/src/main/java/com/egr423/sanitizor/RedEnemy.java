package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Point;

import androidx.annotation.NonNull;

public class RedEnemy extends Enemy {
    public RedEnemy(@NonNull Context context, Point location) {
        super(ENEMY_COLORS.RED, context, location, 6);

        mPointValue = 10;
    }
}
