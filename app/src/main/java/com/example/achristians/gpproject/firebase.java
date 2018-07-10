package com.example.achristians.gpproject;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class firebase extends MainActivity {
    Context context;
    public FirebaseAuth firebaseAuth;
    public FirebaseAuth firebaseInstance() {
        return this.firebaseAuth = FirebaseAuth.getInstance();
    }


    public void signIn(final Context context, String email, String password) {
        boolean isSignedIn = false;
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            User loggedIn = User.getUser();
                            loggedIn.setCurrent_UID(user.getUid());
                            loggedIn.setCurrent_Identifier(user.getEmail());

                            firebaseDB.fetchLoggedInUser();

                            Toast.makeText(context, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }

    public void createUser(final Context context, final String email, final String password, final String name) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            //add newly created user to the users table
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference();
                            DatabaseReference usersRef = ref.child("Users/");
                            HashMap<String, String> coursesCompleted = new HashMap<String,String>();
                            HashMap<String, String> coursesRegistered = new HashMap<String,String>();

                            coursesCompleted.put("100", new Date().toString());
                            coursesRegistered.put("200", new Date().toString());

                            Map<String, User> users = new HashMap<>();

                            Toast.makeText(context, "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            signIn(context, email, password);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWith Email:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
