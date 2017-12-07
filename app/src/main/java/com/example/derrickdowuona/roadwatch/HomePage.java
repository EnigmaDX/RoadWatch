package com.example.derrickdowuona.roadwatch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;

public class HomePage extends AppCompatActivity
{

    TextView uname;
    TextView imgURi;
    ImageView imgView;
    Button uploadImg;
    Button btnTakePhoto;
    Button postBtn;
    String mCurrentPhotoPath;
    FirebaseStorage storage;
    StorageReference storageRef;
    private Uri uri;

    static final int REQUEST_IMAGE_CAPTURE  = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_UPLOAD_PHOTO = 2;
    private static final String TAG = HomePage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("photos");

        //assign all components id
        uname = findViewById(R.id.uName);
        uploadImg = findViewById(R.id.btnUpload);
        imgView = findViewById(R.id.imgView);
        imgURi = findViewById(R.id.imgUri);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        postBtn = findViewById(R.id.btnPost);

        //get data from prev activity and display
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String userName = extras.getString("USERNAME");

        uname.setText(userName);


        //button click to take camera photo
        btnTakePhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
            }
        });

        //button click to open gallery for selecting img
        uploadImg.setOnClickListener(new ImageButton.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_UPLOAD_PHOTO);
            }
        });

        //button click to post report
        postBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //get file path of selected image and upload to storage on firebase
                StorageReference filepath = storageRef.child("photos").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Toast.makeText(HomePage.this,  "Upload Successful", Toast.LENGTH_LONG).show();
                    }
                });
                //
                filepath.putFile(uri).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        // Handle unsuccessful uploads
                        Toast.makeText(HomePage.this,  "FAILED TO UPLOAD", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }//onCreate


    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
            //CAMERA
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
            {
                //insert camera process
            }

            //UPLOAD IMAGE
            else if (requestCode ==REQUEST_UPLOAD_PHOTO && resultCode == RESULT_OK)
            {
                Log.w(TAG, "INSIDE UPLOAD PHOTO SECTION");
                //get uri of selected image and display it in imageview
                uri = data.getData();
                imgURi.setText(uri.toString());
                Bitmap bitmap;
                try
                {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    imgView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                    Log.w(TAG, "FILE NOT FOUND");
                }
//                imgURi.setText(uri.toString());
//                final Bitmap photo = (Bitmap) data.getExtras().get("data");
//                imgView.setImageBitmap(photo);
            }
            else
            {
                Log.w(TAG, "UNKNOWN REQUEST CODE");
            }

            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                imgView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }//END result code

        /**
         *  postBtn.setOnClickListener(new View.OnClickListener()
         {
         @Override
         public void onClick(View view)
         {
         //forward all data to UploadImg page
         Log.w(TAG,"CONTINUE BUTTON CLICKED");
         Intent intent = new Intent(HomePage.this, UploadImg.class);
         intent.putExtra("BitmapImage", photo);
         startActivity(intent);
         }
         });
         */
    }

    public void continueToUpload(Bitmap bitmapImg)
    {
        //forward all data to UploadImg page
        Log.w(TAG,"CONTINUE BUTTON CLICKED");
        Intent intent = new Intent(HomePage.this, UploadImg.class);
        intent.putExtra("BitmapImage", bitmapImg);
        startActivity(intent);
    }


//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        Log.w(TAG, "CurrentPath: " + mCurrentPhotoPath);
//        return image;
//    }
}
