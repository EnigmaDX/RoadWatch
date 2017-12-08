package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;

public class ConfirmReg extends AppCompatActivity {

    private static final String TAG = ConfirmReg.class.getSimpleName();

    TextView uname;
    EditText email;
    EditText password;
    EditText organaisation;
    ProgressBar mProgressBar;
    Button sendCodeBtn;
    EditText phoneText;
    TextView errorText;

    FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    int phone = 0;
    int age = 0;
    String emailStr;
    String userName;
    private String phoneNumber;
    private String orgStr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reg);

        uname = findViewById(R.id.uname);
        email = findViewById(R.id.txtEmail);
        organaisation = findViewById(R.id.txtOrg);
        mProgressBar = findViewById(R.id.progressBar3);
        errorText = findViewById(R.id.errorTxt);
        mProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("user");

        //get data from prev activity and display
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = null;

        if (extras != null) {
            userName = extras.getString("USERNAME");
            emailStr = extras.getString("EMAIL");
            phone = extras.getInt("PHONE");
            age = extras.getInt("AGE");
            orgStr = extras.getString("ORG");
        }

        uname.setText(userName);

         phoneNumber = String.valueOf(phone);

        final String finalUserName = userName;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                //TODO ONCE CODE IS VERIFIED,CALL CREATE USER FXN

                createNewUser(finalUserName,emailStr, phone, age, orgStr);
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                errorText.setText("THERE WAS AN ERROR IN CODE VERification");
                errorText.setVisibility(View.VISIBLE);
            }


            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                errorText.setText("CLICK CONFIRM TO VERIFY CODE");
            }
        };


    }//ENDcreate

    ///USER
    public void createNewUser(String name, String email, int phone, int age, String organaisation)
    {
        Users user = new Users(name, email, phone, age, organaisation);
        String uid = myRef.push().getKey();
        myRef.child(uid).setValue(user);

        Log.w(TAG, "USER DETAILS===================" + user.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String phoneNumber = String.valueOf(phone);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                ConfirmReg.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }//ENDstart



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // TODO CREATE INTENT TO SEND USER TO HOMEPAGE AND CALL FINISH()
                            uname = findViewById(R.id.uname);
                            String uName = uname.getText().toString();
                            Log.d(TAG, "UNAME PASSING TO INTENT==========" + uName);
                            Intent intentHome = new Intent(ConfirmReg.this, HomePage.class);
                            intentHome.putExtra("USERNAME", uName);
                            startActivity(intentHome);
                            finish();
                        }
                        else
                            {
                            // Sign in failed
                            errorText.setText("THERE WAS AN ERROR IN CODE");
                            errorText.setVisibility(View.VISIBLE);
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    //send user data to next page
    public void sendData(String username)
    {
        Log.w(TAG,"SEND DATA FXN CALLED, USERNAME: " + username);
        Intent intentHome = new Intent(ConfirmReg.this, HomePage.class);
//        Bundle extras = new Bundle();
//        extras.putString("USERNAME", username);
//        extras.putString("EMAIL", email);
//        extras.putInt("PHONE", phone);
//        extras.putInt("AGE", age);

        intentHome.putExtra("USERNAME", username);
        startActivity(intentHome);
        finish();
    }


//    public void signInUserClick(View view)
//    {
//        mProgressBar.setVisibility(View.VISIBLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//        password.setEnabled(FALSE);
//        createUser();
//    }

    public void createUser() {
        email = findViewById(R.id.txtEmail);

        final String emailStr = email.getText().toString();
        String passStr = email.getText().toString();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();


        if (extras != null) {
            userName = extras.getString("USERNAME");
            phone = extras.getInt("PHONE");
            age = extras.getInt("AGE");
        }

    }//ENDcreateUser
/*
mAuth.createUserWithEmailAndPassword(emailStr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(ConfirmReg.this, "SUCCESSFULLY REGISTERED", Toast.LENGTH_SHORT).show();

//                            FirebaseUser user = mAuth.getCurrentUser();
                            mProgressBar.setVisibility(View.GONE);

                            signInUser(userName, emailStr, phone, age);
                        }
                        else
                            {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ConfirmReg.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }

                        // ...
                    }
                });
 */


    //END createUser

    /*
    sendData(username, emailStr, phone, age);
    createNewUser(username, emailStr, phone, age);
     */

//    public void signInUser(final String username, final String emailStr, final int phone, final int age){
//
//        password = findViewById(R.id.txtPass);
//        String passStr = email.getText().toString();
//
//        mAuth.signInWithEmailAndPassword(emailStr, passStr)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful())
//                        {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "SIGNIN SUCCESS");
//                            Toast.makeText(ConfirmReg.this, "Successful Sign in", Toast.LENGTH_SHORT).show();
//
//                            sendData(username, emailStr, phone, age);
//                            createNewUser(username, emailStr, phone, age);
//                        }
//                        else
//                            {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "SIGNIN FAILURE", task.getException());
//                            Toast.makeText(ConfirmReg.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }







}
