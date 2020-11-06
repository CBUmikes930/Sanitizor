package com.egr423.sanitizor;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Micah Steinbock on October 21, 2020
 * A manager singleton that is used to interact with the firestore database
 */
public class LeaderboardManager {

    //Singleton instance
    private static LeaderboardManager instance;

    private final String TAG = "FireStore";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    /**
     * The overall method that gets the data from the firestore database and populates the RecyclerView
     * @param context - The context of the app that is calling this method
     * @param rView - The recyclerview to set the content to
     * @param progressBar - The progress bar spinner to hide when the data is generated
     */
    public void populateLeaderboard(Context context, RecyclerView rView, ProgressBar progressBar) {
        mRecyclerView = rView;
        mProgressBar = progressBar;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Find the scores in descending order
        db.collection("scores")
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Data acquired from populateLeaderboard().");
                        //Proecess the results into List<Map> Format
                        List<Map<String, Object>> results = processData(task.getResult());
                        //Populate the adapter
                        ScoreAdapter adapter = new ScoreAdapter(results, context);
                        //Set the recycler view
                        rView.setAdapter(adapter);
                        //Hide progress bar
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        Log.d(TAG,
                                "Could not load documents from populateLeaderboard(): "
                                        + task.getException());
                    }
                });
    }

    /**
     * Method to get the current user's high score (displayed in the leaderboard activity
     * @param context - The activity context that this is getting called from
     * @param textView - The textview to set to the high score
     */
    public void getUserHighScore(Context context, TextView textView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uniqueID;
        if (user != null) {
            Log.d("SignIn", "User found: " + user.getEmail());
            uniqueID = user.getUid();
        } else {
            textView.setText(context.getString(R.string.high_score_sign_in));
            return;
        }

        //Find the scores for instance id
        db.collection("scores")
                .whereEqualTo("instance", uniqueID)
                .orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Data acquired from getUserHighScore().");
                            //Process the results into List<Map> format
                            try {
                                List<Map<String, Object>> results = processData(task.getResult());
                                //Set the TextView
                                if (results.size() > 0) {
                                    textView.setText(results.get(0).get("score").toString());
                                } else {
                                    textView.setText(Integer.toString(0));
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "Error accessing returned data");
                            }
                        } else {
                            Log.d(TAG,
                                    "Could not laod documents from getUserHighScore(): "
                                            + task.getException());
                        }
                    }
                });
    }

    /**
     * Method to add a new score into the database
     * @param context - the activity that this is getting called from
     * @param  - the username to display with the score
     * @param number - the score
     */
    public void addNewScore(Context context, Integer number) {
        //Create data record map
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getDisplayName());
        data.put("score", number);
        data.put("instance", user.getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Add data map into collection
        db.collection("scores")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Data added properly with ID: "
                                + documentReference.getId());
                        //After we add the new data, then refresh the recycler view
                        populateLeaderboard(context, mRecyclerView, mProgressBar);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error adding data: " + e);
                    }
                });
    }


    /**
     * Helper method to convert the QuerySnapshot into a List of Key-Object maps
     * @param result - the result of the query
     * @return - A list of maps that have a string key and object value
     */
    private List<Map<String, Object>> processData(QuerySnapshot result) {
        //Converts a query result set to List<Map> format
        List<Map<String, Object>> results = new ArrayList<>();
        for (DocumentSnapshot doc : result) {
            Log.d(TAG, doc.getData().toString());
            results.add(doc.getData());
        }
        return results;
    }


    private class ScoreHolder extends RecyclerView.ViewHolder {

        private Map<String, Object> mScore;

        private TextView mNameTextView;
        private TextView mScoreTextView;

        public ScoreHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_score, parent, false));
            mNameTextView = itemView.findViewById(R.id.name);
            mScoreTextView = itemView.findViewById(R.id.score);
        }

        public void bind(Map<String, Object> score) {
            mScore = score;
            mNameTextView.setText(mScore.get("name").toString());
            mScoreTextView.setText(mScore.get("score").toString());
        }
    }

    private class ScoreAdapter extends RecyclerView.Adapter<ScoreHolder> {

        private List<Map<String, Object>> mScores;
        private Context mContext;

        public ScoreAdapter(List<Map<String, Object>> scores, Context context) {
            mScores = scores;
            mContext = context;
        }

        @Override
        public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            return new ScoreHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScoreHolder holder, int position) {
            Map<String, Object> score = mScores.get(position);
            holder.bind(score);
        }

        @Override
        public int getItemCount() {
            return mScores.size();
        }
    }
}
