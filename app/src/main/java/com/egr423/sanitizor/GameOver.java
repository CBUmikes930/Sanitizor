package com.egr423.sanitizor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class GameOver extends AppCompatActivity {

    protected void onCreate (Bundle savedInstanceState) {
        //Initialize theme
        SettingsDialogue.initialize(this);
        //create
        super .onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_game_over) ;

        //Initialize the recycler view
        RecyclerView recyclerView = findViewById(R.id.gOrecycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        TextView userScore = findViewById(R.id.score);

        LeaderboardManager leaderboardManager = new LeaderboardManager();
        leaderboardManager.populateLeaderboard(this, recyclerView);



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
}