package com.egr423.sanitizor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.lang.ref.WeakReference;

/**
 * Created by Micah Steinbock - 644014
 * <p>
 * A singleton class that is used to access the sign in/sign out functions
 */
public class AccountManager {

    private static AccountManager mInstance;
    private static WeakReference<Context> mContext;
    //Sign in Intent Code
    private final int CODE = 14142;
    private final GoogleSignInClient mGoogleSignInClient;
    private final FirebaseAuth mAuth;
    private signedInListener mListener;

    private AccountManager(Context context) {
        mContext = new WeakReference<>(context);

        //Load sign-in options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.get().getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext.get(), gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public static AccountManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AccountManager(context);
        }
        setContext(context);

        return mInstance;
    }

    private static void setContext(Context context) {
        mContext = new WeakReference<>(context);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    //Starts the sign in intent for google sign ins
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ((Activity) mContext.get()).startActivityForResult(signInIntent, CODE);
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        mListener.updateUI(null);
    }

    //Called by the activity context upon sign in intent completion
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
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
                .addOnCompleteListener((Activity) mContext.get(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignIn", "SignInWithCredential:Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mListener.updateUI(user);
                        } else {
                            Log.d("SignIn", "SignInWithCredential:Failure", task.getException());
                            mListener.updateUI(null);
                        }
                    }
                });
    }

    //The listener for the completion (updateUI function)
    public void setListener(signedInListener listener) {
        mListener = listener;
    }

    interface signedInListener {
        void updateUI(FirebaseUser user);
    }
}
