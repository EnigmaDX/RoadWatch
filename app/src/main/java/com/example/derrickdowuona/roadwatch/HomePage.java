package com.example.derrickdowuona.roadwatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomePage extends AppCompatActivity {


    TextView uname;
    TextView imgURi;
    ImageView imgView;
    Button uploadImg;
    Button btnTakePhoto;
    String mCurrentPhotoPath;
    StorageReference mStorageRef;
    ProgressDialog mProgress;

    static final int REQUEST_IMAGE_CAPTURE  = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = HomePage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);

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
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }//onCreate


    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_CANCELED)
        {
            //camera mode
            Log.w(TAG, "DATA TO STRING::::"+ data.toString());

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
            {
                mProgress.setMessage("Uploading image");
                mProgress.show();
                Log.w(TAG, "DATA TO STRING::::"+ data.toString());
                Uri uri = data.getData();
                StorageReference filepath = mStorageRef.child("Photos").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgress.dismiss();
                        Toast.makeText(HomePage.this,  "Upload Successful", Toast.LENGTH_LONG).show();
                    }
                });


                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgView.setImageBitmap(photo);
            }
            else
            {
                Log.w(TAG, "FAILED TO SAVE");
            }
        }

        //select image mode
        Uri targetUri = data.getData();
        imgURi.setText(targetUri.toString());
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            imgView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        Log.w(TAG, "CurrentPath: " + mCurrentPhotoPath);
        return image;
    }
}
