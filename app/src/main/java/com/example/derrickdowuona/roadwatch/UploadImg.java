package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class UploadImg extends AppCompatActivity {

    ImageView ImgView;
    EditText txtReport;
    Button cancelBtn;
    Button postBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_img);

        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("BitmapImage");

        ImgView = findViewById(R.id.imgView);
        ImgView.setImageBitmap(bitmap);


    }
}
