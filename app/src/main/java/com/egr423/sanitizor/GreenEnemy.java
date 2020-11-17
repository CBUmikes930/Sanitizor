package com.egr423.sanitizor;

import android.content.Context;
import android.graphics.Point;

import androidx.annotation.NonNull;

public class GreenEnemy extends Enemy {
    // -ONLY SHOOTS-
    private final int TOTAL_SHOTS = 2;
    private final int FRAME_BUFFER = 0;
    private final int framesLeft;
    private boolean greenIsShooting;
    private int shotsLeft;

    public GreenEnemy(@NonNull Context context, Point location) {
        super(ENEMY_COLORS.GREEN, context, location, 6);
        greenIsShooting = false;
        ATTACK_PHASE = 4000;
        framesLeft = FRAME_BUFFER;
        shotCoolDown = 100;
        shotsLeft = TOTAL_SHOTS;
    }

    public void shoot() {
        if (shotsLeft > 0) {
            shotsLeft--;
        } else {
            shotsLeft = TOTAL_SHOTS;
            greenIsShooting = false;
        }
    }

    public boolean getGreenIsShooting() {
        return greenIsShooting;
    }


    public void setGreenIsShooting(boolean isShooting) {
        greenIsShooting = isShooting;
    }
}
