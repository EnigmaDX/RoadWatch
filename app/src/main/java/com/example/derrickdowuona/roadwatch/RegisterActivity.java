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
    private Button buttonSend;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

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
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("user");

        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                verifyAndSendData();
            }
        });
    }

    //END onCreate

    public void verifyAndSendData()
    {
        final String usernameStr = username.getText().toString();
        final String emailStr = email.getText().toString();
        final String phoneStr = phone.getText().toString();
        final String ageStr = age.getText().toString();

        int phoneInt = 0;
        int ageInt = 0;
        try{
            phoneInt = Integer.parseInt(phoneStr);
            Log.i("",phoneInt+" is a number");
        }catch(NumberFormatException ex){
            Log.w(TAG, "EXCEPTIONNNNNNNNNNN");
            Log.i("",phoneInt+" is NOT a number");
            ex.printStackTrace();
        }

        try{
             ageInt = Integer.parseInt(ageStr);
        }catch(NumberFormatException ex){
            Log.w(TAG, "EXCEPTIONNNNNNNNNNN 2222222");
            ex.printStackTrace();
        }

        if(!TextUtils.isEmpty(usernameStr) || !TextUtils.isEmpty(emailStr))
        {
            Intent intent = new Intent(RegisterActivity.this, HomePage.class);
            Bundle extras = new Bundle();
            extras.putString("USERNAME",usernameStr);
            extras.putString("EMAIL",emailStr);
            extras.putInt("PHONE",phoneInt);
            extras.putInt("AGE",ageInt);

            intent.putExtras(extras);
            startActivity(intent);

            Log.w(TAG, "Numbersssssss: " + phoneInt + " AGE:: " + ageInt);
            writeNewUser(usernameStr, emailStr, phoneInt, ageInt);
        }
        else
        {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }



    }

    ///USER
    public void writeNewUser(String name, String email, int phone, int age)
    {
        Users user = new Users(name, email, phone, age);

        String uid = myRef.push().getKey();

        myRef.child(uid).setValue(user);

        Log.w(TAG, "USER DETAILS===================" + user.toString());

        Toast.makeText(RegisterActivity.this, user.toString(), Toast.LENGTH_SHORT).show();

    }





}
