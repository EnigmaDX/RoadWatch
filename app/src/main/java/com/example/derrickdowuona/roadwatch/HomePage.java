package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    private static final String TAG = ConfirmReg.class.getSimpleName();

    TextView unameText;
    Button reportCrimeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);

        Intent homeIntent = getIntent();
        Bundle extras = homeIntent.getExtras();
        String userName = null;

        unameText = findViewById(R.id.username);
        reportCrimeBtn = findViewById(R.id.btnReport);

            userName = getIntent().getStringExtra("USERNAME");
            unameText.setText(userName);

        final String finalUserName = userName;
        reportCrimeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent reportIntent = new Intent(HomePage.this, ReportPage.class);
//                String uname = unameText.getText().toString();
                Log.w(TAG, "UNAAAAAAME::::::::::" + finalUserName);

                if(finalUserName !=null)
                {
                    reportIntent.putExtra("USERNAME", finalUserName);
                    startActivity(reportIntent);
                }
                else
                {
                    Log.w(TAG, "UNAME NOT FOUND!!!!!!!!!");
                }
            }
        });

    }



}
