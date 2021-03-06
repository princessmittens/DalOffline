package com.example.achristians.gpproject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * A Firebase class that does all the back-end operations
 */
public class Firebase {

    //Singleton instance
    private static Firebase instance;

    /**
     * Gets the singleton Firebase instance, instantiating if need be
     * @return Firebase Singleton
     */
    public static Firebase getFirebase(){
        if(instance == null){
            instance = new Firebase();
        }

        return instance;
    }

    /**
     *Initialize the database connection and references
     */
    public static void initializeFirebase(Context appContext){
        instance = new Firebase();
        FirebaseApp.initializeApp(appContext);
        instance.rootDataSource = FirebaseDatabase.getInstance();
        instance.rootDataReference = instance.rootDataSource.getReference();
    }

    //Database references and Authentication instance
    private static FirebaseDatabase rootDataSource;
    private static DatabaseReference rootDataReference;
    private static DatabaseReference usersDataReference;
    private static DatabaseReference generalDataReference;
    private static FirebaseAuth firebaseAuth;

    public static FirebaseAuth getAuth() {
        return instance.firebaseAuth = FirebaseAuth.getInstance();
    }

    public static DatabaseReference getRootDataReference() {
        return instance.rootDataReference;
    }

    public void signIn(final Context context, String email, String password, Activity callingActivity) {
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(callingActivity, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                            User loggedIn = User.getUser();
                            loggedIn.setUID(user.getUid());
                            loggedIn.setIdentifier(user.getEmail());

                    instance.fetchLoggedInUser(context);

                    Toast.makeText(context, "Authentication success.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
                }
            }
        );
    }

    /**
     * Fetches the database information (Registered courses, completed courses)
     * for an authenticated user
     */
    public static void fetchLoggedInUser(final Context context){
        if(User.getUser().getUID() == null || User.getUser().getUID().isEmpty()){
            Toast.makeText(context, "Fetching user data from database failed:UID not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }

        usersDataReference = rootDataReference.child("Users").child(User.getUser().getUID());

        usersDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (u == null || u.getIdentifier() == null) {
                    u = User.getUser();
                }
                User.setUser(u);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Creates a new Authenticated user with email and password
     * @param context App context to run with
     * @param email Email to use for creation
     * @param password Password to use for creation
     * @param name Identified/Username for creation
     * @param callingActivity Activity to run on (Required for onCompleteListeners
     */
    public void createUser(final Context context, final String email, final String password, final String name, final Activity callingActivity) {
        if(firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(callingActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Re-enabling the register button (Not strictly necessary as it should
                            //leave the registration page)
                            SignUpPage.regButtonPressed = false;

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            Toast.makeText(context, "User created.",
                                    Toast.LENGTH_SHORT).show();
                            signIn(context, email, password, callingActivity);

                        } else {
                            //Re-enabling the register button if this failed.
                            SignUpPage.regButtonPressed = false;

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWith Email:failure", task.getException());
                            Toast.makeText(context, "User creation failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            );
        }
        catch (Exception e){
            Toast.makeText(context, "Fatal error when creating user remotely.", Toast.LENGTH_SHORT).show();
            SignUpPage.regButtonPressed = false;
        }
    }
}
