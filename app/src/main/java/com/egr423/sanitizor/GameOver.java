package com.egr423.sanitizor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameOver extends AppCompatActivity implements AccountManager.signedInListener {

    //The score for the run
    private int mScore;

    protected void onCreate (Bundle savedInstanceState) {
        //Initialize theme
        SettingsDialogue.initialize(this);
        //create
        super .onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_game_over) ;

        //Initialize the recycler view
        RecyclerView recyclerView = findViewById(R.id.gOrecycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add seperator between records
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        //Get the score from the intent
        Intent gameOverIntent = getIntent();
        mScore = gameOverIntent.getIntExtra("com.egr423.sanitizor.score", 0);
        //Display score
        TextView userScore = findViewById(R.id.score);
        userScore.setText(Integer.toString(mScore));

        //Load leaderboard
        ProgressBar progressBar = findViewById(R.id.progressBar1);
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        leaderboardManager.populateLeaderboard(this, recyclerView, progressBar);

        //Load header info
        TextView textView = findViewById(R.id.GameOverTitle) ;
        SpannableString content = new SpannableString( "Game Over" ) ;
        content.setSpan( new UnderlineSpan() , 0 , content.length() , 0 ) ;
        textView.setText(content);

        //Update UI based on current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updateUI(user);
    }

    public void playAgainButton(View view) {
        Intent gameIntent = new Intent(GameOver.this, GameActivity.class);
        startActivity(gameIntent);
    }

    public void HomeButton(View view) {
        Intent gameIntent = new Intent(GameOver.this, MainActivity.class);
        startActivity(gameIntent);
    }

    public void logIn(View view) {
        AccountManager accountManager = AccountManager.getInstance(this);
        accountManager.setListener(this::updateUI);
        accountManager.signIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AccountManager.getInstance(this).handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void updateUI(FirebaseUser user) {
        if (user == null) {
            // Show sign in options
            findViewById(R.id.sign_in_text).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        } else {
            // Hide sign in options
            findViewById(R.id.sign_in_text).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            // Submit score into database
            LeaderboardManager.getInstance().addNewScore(this, mScore);
        }
    }
}