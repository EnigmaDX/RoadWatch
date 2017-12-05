package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomePage extends AppCompatActivity {


    TextView uname;
    TextView imgURi;
    ImageView imgView;
    Button uploadImg;
    Button btnTakePhoto;
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE  = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = HomePage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        //get data from prev activity and display
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = extras.getString("USERNAME");
        uname = findViewById(R.id.uName);
        uname.setText(userName);


        //get ids of components
        uploadImg = findViewById(R.id.btnUpload);
        imgView = findViewById(R.id.imgView);
        imgURi = findViewById(R.id.imgUri);

        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        btnTakePhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
            }
        });

        //fxn to open gallery for selecting img
        uploadImg.setOnClickListener(new ImageButton.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                //
            }
        });
    }//onCreate


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.w(TAG, "COULD NOT CREATE FILE");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.w(TAG, "PHOTOFILE NOT NULL");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                Log.w(TAG,  "photouri::::::" + photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            else{
                Log.w(TAG, "PHOTO URI NOT FOUND");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_CANCELED)
        {
            Log.w(TAG, "DATA TO STRIG::::"+ data.toString());
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
//                Bundle extras = data.getExtras();
                Log.w(TAG, "DATA TO STRIG::::"+ data.toString());
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgView.setImageBitmap(photo);
            }
            else
            {
                Log.w(TAG, "FAILED TO SAVE");
            }
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
