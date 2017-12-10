package com.example.derrickdowuona.roadwatch;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportPage extends AppCompatActivity
{

    TextView uname;
    TextView imgURi;
    ImageView imgView;
    Button uploadImg;
    Button btnTakePhoto;
    Button postBtn;
    EditText description;
    ProgressBar mProgressBar;
    String mCurrentPhotoPath;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private Uri FilePathUri;

    // Folder path for Firebase Storage.

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
        storageRef = storage.getReference("Images/");

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("report");

        mProgressBar = findViewById(R.id.progressBar2);
        mProgressBar.setVisibility(View.GONE);

        //assign all components id
        uname = findViewById(R.id.uName);
        uploadImg = findViewById(R.id.btnUpload);
        imgView = findViewById(R.id.imgView);
        imgURi = findViewById(R.id.imgUri);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        postBtn = findViewById(R.id.btnPost);
        description = findViewById(R.id.txtDescr);

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
               // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
////                // Ensure that there's a camera activity to handle the intent
////                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
//                {
//                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
////                }
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
                final String descr = description.getText().toString();

                if (imgView.getDrawable() == null || FilePathUri == null || descr.isEmpty())
                {
                    Toast.makeText(ReportPage.this,  "Please upload media and write comment first", Toast.LENGTH_LONG).show();
                }
                else
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    //get file path of selected image and upload to storage on firebase
                    currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    final String displayName = uname.getText().toString();


//                    StorageReference filepath = storageRef.child("photos" + uid + "/").child(Storage_Path + System.currentTimeMillis()
//                            + "." + GetFileExtension(FilePathUri));

                    StorageReference filepath = storageRef.child(uid + "/").child(System.currentTimeMillis()
                            + "." + GetFileExtension(FilePathUri));

                    Log.w(TAG, "FILEPATH: " + filepath);

                    filepath.putFile(FilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();

                            createNewReport(downloadUri, descr, displayName);
                            Log.w(TAG, "URI: " + FilePathUri + "_______DOWNLOAD URI::::; " + downloadUri);

                            mProgressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            Intent backIntent = new Intent(ReportPage.this, HomePage.class);
                            startActivity(backIntent);

                            Toast.makeText(ReportPage.this,  "Upload Successful", Toast.LENGTH_LONG).show();

                            description.setText(null);

                        }
                    });
                    //
                    filepath.putFile(FilePathUri).addOnFailureListener(new OnFailureListener()
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


    @Override
    public void onStart() {
        super.onStart();
        // If user is signed in, got to homepage and never return here again!!
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            String userID = currentUser.getUid();
            uname.setText(userID);
            uname.setVisibility(View.VISIBLE);
            Log.d(TAG, "USERS ID+++++++++++++++++++++++" + userID);
//            register.setVisibility(View.INVISIBLE);
        }
        else
        {
            Log.d(TAG, "NOOOOOOOOOOO USSSSSSSSSSSEEEEEEEEEEEERRRRRRRRRRRRRR");
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                Uri photoURI = FileProvider.getUriForFile(this,authorities,photoFile);
                Log.w(TAG, "PHOTO URIIIIIIIIIIIIIIIIIIIII== " + photoURI);
                Log.w(TAG, "PHOTO FILLLLLLLLLEEEEEEEEEEEE== " + photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            else
            {
                Toast.makeText(ReportPage.this,  "PHOTO FILE IS NULLLLLLLL", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_CANCELED && data !=null)
        {
            mProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            //CAMERA
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
            {
                //insert camera process
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgView.setImageBitmap(imageBitmap);
                Toast.makeText(ReportPage.this,  "Gotten Camera image", Toast.LENGTH_LONG).show();
            }

            //UPLOAD IMAGE
            else if (requestCode ==REQUEST_UPLOAD_PHOTO && resultCode == RESULT_OK)
            {
                    Log.w(TAG, "INSIDE UPLOAD PHOTO SECTION");
                    //get uri of selected image and display it in imageview
                    FilePathUri = data.getData();
                    imgURi.setText(FilePathUri.toString());
                    Bitmap bitmap;
                    try
                    {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(FilePathUri));
                        imgView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                        Log.w(TAG, "FILE NOT FOUND");
                    }
            }
            else
            {
                Log.w(TAG, "UNKNOWN REQUEST CODE");
            }
        }
        else
        {
            Toast.makeText(ReportPage.this,  "DATA NULL////////////" + data, Toast.LENGTH_LONG).show();
        }
    }//END result code}

    ///POST OBJECT
    public void createNewReport(String uri, String description, String username)
    {
        Report report = new Report(uri, description, username);
        String uid = myRef.push().getKey();
        myRef.child(uid).setValue(report);

        Log.w(TAG, "POST DETAILS===================" + report.toString());
    }


        // Creating Method to get the selected image file Extension from File Path URI.
        public String GetFileExtension(Uri uri)
        {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }
}
