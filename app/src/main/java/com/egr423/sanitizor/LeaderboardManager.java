package com.egr423.sanitizor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Micah Steinbock on October 21, 2020
 * A manager object that is used to interact with the firestore database
 */
public class LeaderboardManager {

    private final String TAG = "FireStore";

    /**
     * The overall method that gets the data from the firestore database and populates the RecyclerView
     * @param context - The context of the app that is calling this method
     * @param rView - The recyclerview to set the content to
     */
    public void populateLeaderboard(Context context, RecyclerView rView) {
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
                    } else {
                        Log.d(TAG,
                                "Could not load documents from populateLeaderboard(): "
                                        + task.getException());
                    }
                });
    }

    /**
     * Method to get the current user's high score (displayed in the leaderboard activity
     * @param textView - The textview to set to the high score
     * @param user - The name of the current user to search for
     */
    public void getUserHighScore(TextView textView, String user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Find the scores for user
        db.collection("scores")
                .whereEqualTo("name", user)
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
                                //Sset the TextView
                                textView.setText(results.get(0).get("score").toString());
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

    public void addNewScore(String name, Integer number) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("score", number);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("scores")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Data added properly with ID: "
                                + documentReference.getId());
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
