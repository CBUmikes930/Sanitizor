package com.egr423.sanitizor;

import android.content.Context;

import androidx.annotation.NonNull;

public class lifePowerup extends PowerUp {
    public lifePowerup(@NonNull Context context) {
        super(POWER_UPS.LIFE, context);
    }

    @Override
    public void upgradePlayer(Player player) {
        player.setPlayerLives(player.getPlayerLives() + 1);
        SoundManager.getInstance().playSound("Powerup.ogg");
    }
}