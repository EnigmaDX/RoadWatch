package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmReg extends AppCompatActivity {

    private static final String TAG = ConfirmReg.class.getSimpleName();

    TextView uname;
    EditText email;
    EditText password;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    int phone = 0;
    int age = 0;
    String emailStr;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reg);

        Log.w(TAG, "ATTN:: CREATED CONFIRM REGISTER PAGE");

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("user");

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //get data from prev activity and display
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = null;

        if (extras != null) {
            userName = extras.getString("USERNAME");
            emailStr = extras.getString("EMAIL");
            phone = extras.getInt("PHONE");
            age = extras.getInt("AGE");
        }
        uname = findViewById(R.id.uname);
        uname.setText(userName);
        email = findViewById(R.id.txtEmail);
        email.setText(emailStr);

        password = findViewById(R.id.txtPass);
    }

    public void signInUserClick(View view)
    {
        progressBar.setVisibility(View.VISIBLE);
        createUser();
    }

    public void createUser()
    {
        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPass);

        final String emailStr = email.getText().toString();
        String passStr = email.getText().toString();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();


        if (extras != null) {
            userName = extras.getString("USERNAME");
            phone = extras.getInt("PHONE");
            age = extras.getInt("AGE");
        }

        mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(ConfirmReg.this, "Successful Sign in", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressBar.setVisibility(View.GONE);
                            signInUser(userName, emailStr, phone, age);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ConfirmReg.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                        // ...
                    }
                });
    }
    //END createUser


    public void signInUser(final String username, final String emailStr, final int phone, final int age){

        password = findViewById(R.id.txtPass);
        String passStr = email.getText().toString();

        mAuth.signInWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "SIGNIN SUCCESS");
                            Toast.makeText(ConfirmReg.this, "Successful Sign in", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                            sendData(username, emailStr, phone, age);
                            writeNewUser(username, emailStr, phone, age);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "SIGNIN FAILURE", task.getException());
                            Toast.makeText(ConfirmReg.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    //send user data to next page
    public void sendData(String username, String email, int phone, int age)
    {
        Intent intentHome = new Intent(ConfirmReg.this, HomePage.class);
        Bundle extras = new Bundle();
        extras.putString("USERNAME", username);
        extras.putString("EMAIL", email);
        extras.putInt("PHONE", phone);
        extras.putInt("AGE", age);

        intentHome.putExtras(extras);
        startActivity(intentHome);
    }

    ///USER
    public void writeNewUser(String name, String email, int phone, int age)
    {
        Users user = new Users(name, email, phone, age);

        String uid = myRef.push().getKey();

        myRef.child(uid).setValue(user);

        Log.w(TAG, "USER DETAILS===================" + user.toString());

        Toast.makeText(ConfirmReg.this, user.toString(), Toast.LENGTH_SHORT).show();
    }






}
