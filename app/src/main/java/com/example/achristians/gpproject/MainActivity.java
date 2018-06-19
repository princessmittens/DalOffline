package com.example.achristians.gpproject;


import android.nfc.Tag;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// assumes user open to availibility of course with list view
public class MainActivity extends AppCompatActivity {


    private Button btnNavigateCourseDescription;
    public static String courseIDstring;
    TextView Vlogpass, Vlogemail;
    EditText Elogpass, Elogemail, Echeckpass;
    Button createaccount, loginbutton;
    public static FirebaseAuth firebaseAuth;
    public static firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        courseIDstring="CourseID";



        setContentView(R.layout.login);

        Vlogemail = findViewById(R.id.Vlogemail);
        Vlogpass = findViewById(R.id.Vlogpass);
        Elogpass = findViewById(R.id.Elogpass);
        Echeckpass = findViewById(R.id.Echeckpass);
        Elogemail = findViewById(R.id.Elogemail);

        createaccount = findViewById(R.id.createaccount);
        loginbutton = findViewById(R.id.loginbutton);
        fb = new firebase();
        firebaseAuth = fb.firebaseInstance();


        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, signUpPage.class);
                startActivity(i);
            }
        });


        final Context context = this;
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Elogemail.getText().toString();
                String pass = Elogpass.getText().toString();

                String secondPass = Echeckpass.getText().toString();
              
                fb.signIn(MainActivity.this, email, pass);
                Intent i = new Intent(getApplicationContext() ,MainActivity.class);
                startActivity(i);
            }
        });


        btnNavigateCourseDescription=findViewById(R.id.navigateCourseDescription);

        btnNavigateCourseDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),courseDetails.class);
                intent.putExtra(courseIDstring,"CSCI3130");
                startActivity(intent);
            }
        });


    }

    public FirebaseAuth.AuthStateListener mAuth = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                Intent i = new Intent(MainActivity.this, registration.class);
                startActivity(i);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();
        }
        firebaseAuth.addAuthStateListener(mAuth);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        auth.removeAuthStateListener(authStateListener);
    }
}
