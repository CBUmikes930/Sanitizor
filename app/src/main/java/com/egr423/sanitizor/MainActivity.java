package com.egr423.sanitizor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsDialogue.initialize(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_unlogged);
    }

    /**
     * Added by Micah Steinbock on 10/6/2020
     *
     * A method that is triggered by the "Play" button
     * Starts the GameActivity
     * @param view
     */
    public void playButton(View view) {
        Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(gameIntent);
    }

    /**
     * Added by Chase Crossley on 10/6/2020
     *
     * A method that is triggered by the "Settings" button
     * Opens the modal Settings page
     * @param view
     */
    public void settingsButtonListener(View view) {
        SettingsDialogue settingsDialogue = new SettingsDialogue();
        settingsDialogue.show(getSupportFragmentManager(), "settingsFragment");
    }

    public void viewLeaderboard(View view) {
        Intent leaderboardIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
        startActivity(leaderboardIntent);
    }

    @Override
    public void recreate() {
        super.recreate();
    }

}