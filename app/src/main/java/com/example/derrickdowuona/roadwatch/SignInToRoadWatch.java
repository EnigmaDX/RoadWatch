package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignInToRoadWatch extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    EditText email;
    EditText password;
    Button signin;
    Button register;
    String emailStr;
    String userNameStr;
    FirebaseAuth mAuth;
    ProgressBar loginBar;
    TextView unameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
//        passwordStr = extras.getString("PASS");

        email = findViewById(R.id.emailtxt);
        password = findViewById(R.id.passwordtxt);
        signin = findViewById(R.id.signInBtn);
        register = findViewById(R.id.regBtn);
        loginBar = findViewById(R.id.logInBar);
        unameText = findViewById(R.id.username);
        password = findViewById(R.id.passwordtxt);

//        userNameStr = getIntent().getStringExtra("USERNAME");
//        unameText.setText(userNameStr);

        loginBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        emailStr = email.getText().toString();
        final String passwordStr = password.getText().toString();
        Log.w(TAG, "Password content=============" + passwordStr);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(!(emailStr.equals(""))  !(passwordStr.equals("")))
//                {
                    signInUser(emailStr, passwordStr);

//                else
//                {
//                    Toast.makeText(SignInToRoadWatch.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//                }

            }
        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intentReg = new Intent(SignInToRoadWatch.this, RegisterActivity.class);
                startActivity(intentReg);
                finish();
            }
        });

    }//end ONcREATE


    public void signInUser(String emaill, String passwordd)
    {
            loginBar.setVisibility(View.VISIBLE);
        String emailStr = email.getText().toString();
        String passStr = password.getText().toString();

            //sign in user with email and password
        Log.d(TAG, "em pass+++++++++++++++++++++++" + passStr);
            mAuth.signInWithEmailAndPassword(emailStr, passStr)
                    .addOnCompleteListener(SignInToRoadWatch.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(SignInToRoadWatch.this, "Successful Sign in", Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.w(TAG, "current user::::" + user);
//                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(userNameStr).build();
//                                user.updateProfile(profileUpdate);

                                Intent intentHome = new Intent(SignInToRoadWatch.this, HomePage.class);
                                startActivity(intentHome);
                                finish();
                            }
                            else
                                {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInToRoadWatch.this, "SIGN IN failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                            // ...
                        }
                    });
    }

}
