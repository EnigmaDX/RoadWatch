package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class HomePage extends AppCompatActivity {

    private static final String TAG = ConfirmReg.class.getSimpleName();

    TextView unameText;
    Button reportCrimeBtn;
    Button logoutBtn;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);

        mAuth = FirebaseAuth.getInstance();

        Intent homeIntent = getIntent();
        Bundle extras = homeIntent.getExtras();
        String userName = null;

        if(extras !=null)
        {
            userName = extras.getString("USERNAME");
        }
        else
        {
            Toast.makeText(HomePage.this, "Username from extra intent null", Toast.LENGTH_SHORT).show();
        }


        unameText = findViewById(R.id.username);
        reportCrimeBtn = findViewById(R.id.btnReport);
        logoutBtn = findViewById(R.id.btnLogout);


            //logout user
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendBackToMainPage();
            }
        });

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(userName).build();
//        currentUser.updateProfile(profileUpdate);

        final String finalUserName = userName;
        reportCrimeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent reportIntent = new Intent(HomePage.this, ReportPage.class);
//                String uname = unameText.getText().toString();
                Log.w(TAG, "ID::::::::::" + currentUser.getUid());
//                    reportIntent.putExtra("USERNAME", finalUserName);
                startActivity(reportIntent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // If user is signed in, got to homepage and never return here again!!
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            String userID = currentUser.getUid();
            unameText.setText(userID);
            unameText.setVisibility(View.VISIBLE);
            Log.d(TAG, "USERS ID+++++++++++++++++++++++" + userID);
//            register.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d(TAG, "NOOOOOOOOOOO USSSSSSSSSSSEEEEEEEEEEEERRRRRRRRRRRRRR");
        }
    }


    public void sendBackToMainPage()
    {
        Intent backIntent = new Intent(HomePage.this, RegisterActivity.class);
        finish();
    }



}
