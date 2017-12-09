package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText username;
    private EditText email;
    private EditText phone;
    private EditText age;
    private EditText organisation;
    private EditText password;
    private Button buttonSend;
    private Button login;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;


    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.txtName);
        email    = findViewById(R.id.txtEmail);
        phone    = findViewById(R.id.txtPhone);
        age      = findViewById(R.id.txtAge);
        organisation = findViewById(R.id.txtOrg);
        buttonSend = findViewById(R.id.btnReg);
        login = findViewById(R.id.btnLogin);
        password = findViewById(R.id.txtPass);

        mAuth = FirebaseAuth.getInstance();

        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                verifyAndSendData();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentSignIn = new Intent(RegisterActivity.this, SignInToRoadWatch.class);
                startActivity(intentSignIn);
                finish();
            }
        });

    }//END onCreate


    @Override
    public void onStart() {
        super.onStart();
        // If user is signed in, got to homepage and never return here again!!
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            Intent homeIntent = new Intent(RegisterActivity.this, HomePage.class);
            String userName = currentUser.getDisplayName();

            Bundle extras = new Bundle();
            extras.putString("USERNAME",userName);
            homeIntent.putExtras(extras);
            startActivity(homeIntent);
        }
    }

    public void verifyAndSendData()
    {
        final String usernameStr = username.getText().toString();
        final String emailStr = email.getText().toString();
        final String phoneStr = phone.getText().toString();
        final String ageStr = age.getText().toString();
        final String organisationStr = organisation.getText().toString();
        final String passStr = password.getText().toString();

        int phoneInt = 0;
        int ageInt = 0;
        try{
            phoneInt = Integer.parseInt(phoneStr);
            Log.i("",phoneInt+" is a number");
        }catch(NumberFormatException ex){
            Log.i("",phoneInt+" is NOT a number");
            ex.printStackTrace();
        }

        try{
             ageInt = Integer.parseInt(ageStr);
        }catch(NumberFormatException ex){
            Log.i("",ageInt+" is NOT a number");
            ex.printStackTrace();
        }

        if(!(usernameStr.equals("")) && !(emailStr.equals("")) && !(phoneStr.equals("")) && !(ageStr.equals("")) && !(passStr.equals("")))
        {
            Intent intentConfirm = new Intent(RegisterActivity.this, SignInToRoadWatch.class);
            Bundle extras = new Bundle();
            extras.putString("USERNAME",usernameStr);
            extras.putString("EMAIL",emailStr);
            extras.putString("PASS",passStr);
            extras.putInt("PHONE",phoneInt);
            extras.putInt("AGE",ageInt);
            extras.putString("ORG",organisationStr);


            createUserEmail(emailStr,passStr);

            intentConfirm.putExtras(extras);
            startActivity(intentConfirm);
            finish();
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void createUserEmail(String emailStrr, String passStrr)
    {
         emailStrr = email.getText().toString();
         passStrr = phone.getText().toString();
        //create user with email
        mAuth.createUserWithEmailAndPassword(emailStrr, passStrr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Created",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }




}
