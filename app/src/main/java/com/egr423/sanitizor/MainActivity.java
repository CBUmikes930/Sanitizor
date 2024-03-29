package com.egr423.sanitizor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements AccountManager.signedInListener {

    private AccountManager mAccountManager;
    private SoundManager mSoundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsDialogue.initialize(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        mAccountManager = AccountManager.getInstance(this);
        mAccountManager.setListener(this::updateUI);

        mSoundManager = SoundManager.getInstance();
        mSoundManager.setAudioStatus(SettingsDialogue.playGameAudio);
        mSoundManager.loadSounds(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSoundManager.stopAll();
        mSoundManager.playSound("Home_Screen.ogg", -1);

        updateUI(mAccountManager.getCurrentUser());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSoundManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.getInstance().pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundManager.getInstance().stopSound("Home_Screen.ogg");
    }

    /**
     * Added by Micah Steinbock on 10/6/2020
     * <p>
     * A method that is triggered by the "Play" button
     * Starts the GameActivity
     *
     * @param view
     */
    public void playButton(View view) {
        Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(gameIntent);
    }

    /**
     * Added by Chase Crossley on 10/6/2020
     * <p>
     * A method that is triggered by the "Settings" button
     * Opens the modal Settings page
     *
     * @param view
     */
    public void settingsButtonListener(View view) {
        SettingsDialogue settingsDialogue = new SettingsDialogue();
        settingsDialogue.show(getSupportFragmentManager(), "settingsFragment");
        settingsDialogue.hideThemeOption(false);
    }

    /**
     * Added by Micah Steinbock on 10/20/2020
     * <p>
     * A method that is triggered by the "Leaderboard" button
     * Starts and switches to the Leaderboard activity
     *
     * @param view
     */
    public void viewLeaderboard(View view) {
        Intent leaderboardIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
        startActivity(leaderboardIntent);
    }

    /**
     * Added by Micah Steinbock on 11/4/2020
     * <p>
     * A method that is triggered by the "Log in" button
     * Starts the google sign in intent
     * triggers onActivityReuslt when completed
     *
     * @param view
     */
    public void signIn(View view) {
        mAccountManager.signIn();
    }

    /**
     * Added by Micah Steinbock on 11/4/2020
     * <p>
     * A method that is triggered by the "Log out" button
     * Signs out the currently logged in user and updates the UI
     *
     * @param view
     */
    public void signOut(View view) {
        mAccountManager.signOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mAccountManager.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateUI(FirebaseUser user) {
        if (user != null) {
            findViewById(R.id.button_signin).setVisibility(View.INVISIBLE);
            findViewById(R.id.button_signout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.button_signin).setVisibility(View.VISIBLE);
            findViewById(R.id.button_signout).setVisibility(View.INVISIBLE);
        }
    }
}