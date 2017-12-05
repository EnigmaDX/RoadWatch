package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private String mVerificationId;

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText username;
    private EditText email;
    private EditText phone;
    private EditText age;
    private Button buttonSend;

    private FirebaseAuth mAuth;

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.txtName);
        email    = findViewById(R.id.txtEmail);
        phone    = findViewById(R.id.txtPhone);
        age      = findViewById(R.id.txtAge);
        buttonSend = findViewById(R.id.btnReg);

        mAuth = FirebaseAuth.getInstance();

        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String phoneStr = phone.getText().toString();
        String ageStr = age.getText().toString();

        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                verifyAndSendData();
            }
        });

    }
    //END onCreate

    public void verifyAndSendData()
    {
        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String phoneStr = phone.getText().toString();
        String ageStr = age.getText().toString();

        Intent intent = new Intent(RegisterActivity.this, HomePage.class);
        Bundle extras = new Bundle();
        extras.putString("USERNAME",usernameStr);
        extras.putString("EMAIL",emailStr);
        extras.putString("PHONE",phoneStr);
        extras.putString("AGE",ageStr);

        //call verify method for phone
        startPhoneNumberVerification(phoneStr);

        intent.putExtras(extras);
        startActivity(intent);
    }

    ////////////////////////////REGGG
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    ///




}
