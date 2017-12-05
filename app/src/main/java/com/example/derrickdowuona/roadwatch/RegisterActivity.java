package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText phone;
    private EditText age;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.txtName);
        email    = findViewById(R.id.txtEmail);
        phone    = findViewById(R.id.txtPhone);
        age      = findViewById(R.id.txtAge);
        buttonSend = findViewById(R.id.btnReg);

        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String phoneStr = phone.getText().toString();
        String ageStr = age.getText().toString();

        buttonSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

    }

    public void sendData()
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

        intent.putExtras(extras);
        startActivity(intent);
    }
}
