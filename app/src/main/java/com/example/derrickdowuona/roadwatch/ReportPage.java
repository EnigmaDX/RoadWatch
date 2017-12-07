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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;

public class ReportPage extends AppCompatActivity
{

    TextView uname;
    TextView imgURi;
    ImageView imgView;
    Button uploadImg;
    Button btnTakePhoto;
    Button postBtn;
    ProgressBar mProgressBar;
    String mCurrentPhotoPath;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    private Uri uri;

    static final int REQUEST_IMAGE_CAPTURE  = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_UPLOAD_PHOTO = 2;
    private static final String TAG = ReportPage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("photos");

        mProgressBar = findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.GONE);

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
        String userName = getIntent().getStringExtra("USERNAME");

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
                if (imgView.getDrawable() == null || uri == null)
                {
                    Toast.makeText(ReportPage.this,  "Please upload an image first", Toast.LENGTH_LONG).show();
                }
                else
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    //get file path of selected image and upload to storage on firebase
                    currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    StorageReference filepath = storageRef.child("photos/" + uid + "/").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(ReportPage.this,  "Upload Successful", Toast.LENGTH_LONG).show();
                        }
                    });
                    //
                    filepath.putFile(uri).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            // Handle unsuccessful uploads
                            Toast.makeText(ReportPage.this,  "FAILED TO UPLOAD", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }//onCreate

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }


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
            mProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
         Intent intent = new Intent(ReportPage.this, UploadImg.class);
         intent.putExtra("BitmapImage", photo);
         startActivity(intent);
         }
         });
         */
    }

//    public void uploadImage()

    public void continueToUpload(Bitmap bitmapImg)
    {
        //forward all data to UploadImg page
        Log.w(TAG,"CONTINUE BUTTON CLICKED");
        Intent intent = new Intent(ReportPage.this, UploadImg.class);
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
