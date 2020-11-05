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

public class GameOver extends AppCompatActivity {

    //The score for the run
    private int mScore;

    private final int NUM_CHARS = 3;
    private final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ012345679";

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
        TextView userScore = findViewById(R.id.score);
        userScore.setText(Integer.toString(mScore));

        ProgressBar progressBar = findViewById(R.id.progressBar1);
        //Add the score into the database
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        //Load the recycler view
        leaderboardManager.populateLeaderboard(this, recyclerView, progressBar);

        //Load header info
        TextView textView = findViewById(R.id.GameOverTitle) ;
        SpannableString content = new SpannableString( "Game Over" ) ;
        content.setSpan( new UnderlineSpan() , 0 , content.length() , 0 ) ;
        textView.setText(content) ;
    }

    public void playAgainButton(View view) {
        Intent gameIntent = new Intent(GameOver.this, GameActivity.class);
        startActivity(gameIntent);
    }

    public void HomeButton(View view) {
        Intent gameIntent = new Intent(GameOver.this, MainActivity.class);
        startActivity(gameIntent);
    }

    public void submitScore(View view) {
        //Get name
        String name = getName();
        //Add score to database with name
        LeaderboardManager leaderboardManager = LeaderboardManager.getInstance();
        leaderboardManager.addNewScore(this, name, mScore);
        //Toggle buttons at the bottom
        view.setVisibility(View.INVISIBLE);
        findViewById(R.id.button_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.name_layout).setVisibility(View.GONE);
    }

    //Generates the name string based off of the three textviews
    private String getName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= NUM_CHARS; i++) {
            int id = getResources().getIdentifier("letter_" + i + "_text", "id",
                    getApplicationContext().getPackageName());
            sb.append(((TextView) findViewById(id)).getText());
        }
        return sb.toString();
    }

    public void arrowClick(View view) {
        //Get the name of the clicked button
        String name = getResources().getResourceEntryName(view.getId());
        //Get the last section of the name (up/down)
        String action = name.substring(name.lastIndexOf('_') + 1);
        //Get the prefix of the name (letter_[x])
        String number = name.substring(0, name.lastIndexOf('_'));
        //Get the matching Text View (letter_[x]_text)
        TextView textView = findViewById(getResources().getIdentifier(number + "_text", "id",
                getApplicationContext().getPackageName()));

        //Extract the current character
        char curChar = textView.getText().charAt(0);
        //If the action is up, then shift is positive, otherwise its negative
        int shift = (action.equals("up")) ? 1 : -1;
        //Calculate new char
        int charIndex = (ALLOWED_CHARS.indexOf(curChar) + shift) % ALLOWED_CHARS.length();
        if (charIndex < 0) {
            charIndex += ALLOWED_CHARS.length();
        }
        char newChar = ALLOWED_CHARS.charAt(charIndex);
        //Set new character
        textView.setText(java.lang.Character.toString(newChar));
    }
}