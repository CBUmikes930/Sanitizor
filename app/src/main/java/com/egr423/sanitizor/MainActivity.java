package com.egr423.sanitizor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    //Sign in Intent Code
    private final int CODE = 14142;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsDialogue.initialize(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        //Load sign-in options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is currently logged in
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
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

    /**
     * Added by Micah Steinbock on 10/20/2020
     *
     * A method that is triggered by the "Leaderboard" button
     * Starts and switches to the Leaderboard activity
     * @param view
     */
    public void viewLeaderboard(View view) {
        Intent leaderboardIntent = new Intent(MainActivity.this, LeaderboardActivity.class);
        startActivity(leaderboardIntent);
    }

    /**
     * Added by Micah Steinbock on 11/4/2020
     *
     * A method that is triggered by the "Log in" button
     * Starts the google sign in intent
     * triggers onActivityReuslt when completed
     * @param view
     */
    public void signIn(View view) {
        Log.d("TEST", "Sign In Pressed");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, CODE);
    }

    /**
     * Added by Micah Steinbock on 11/4/2020
     *
     * A method that is triggered by the "Log out" button
     * Signs out the currently logged in user and updates the UI
     * @param view
     */
    public void signOut(View view) {
        Log.d("TEST", "Sign Out Pressed");
        FirebaseAuth.getInstance().signOut();
        updateUI(null);

        Toast toast = Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If the activity result is from the desired intent (code)
        if (requestCode == CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Try to get the logged in account
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.d("SignIn", "SignInResult:Failed Code=" + e.getStatusCode());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        //Sign in the user based on the google idToken
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignIn", "SignInWithCredential:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.d("SignIn", "SignInWithCredential:Failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("SignIn", "Sign in successful");
            findViewById(R.id.button_signin).setVisibility(View.INVISIBLE);
            findViewById(R.id.button_signout).setVisibility(View.VISIBLE);
            Toast toast = Toast.makeText(this, "Sign-In Successful", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Log.d("SignIn", "Sign in unsuccessful");
            findViewById(R.id.button_signin).setVisibility(View.VISIBLE);
            findViewById(R.id.button_signout).setVisibility(View.INVISIBLE);
        }
    }
}