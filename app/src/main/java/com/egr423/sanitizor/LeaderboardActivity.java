package com.egr423.sanitizor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize theme
        SettingsDialogue.initialize(this);
        //Create
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        //Initialize the reycler view
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Add a divider between elements in the recycler view
        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        ProgressBar progressBar = findViewById(R.id.progressBar1);

        //Use LeaderboardManager to populate the recycler view
        LeaderboardManager leaderboardManager = new LeaderboardManager();
        leaderboardManager.populateLeaderboard(this, recyclerView, progressBar);
        TextView userHighScore = findViewById(R.id.highscore);
        //Temporary literal
        leaderboardManager.getUserHighScore(this, userHighScore);
    }

    //Return to the parent activity
    public void homeButton(View view) {
        finish();
    }
}