package com.example.cameraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private ImageView mImageView;
    private Button mCameraButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Uri mCurrentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mCameraButton = (Button) findViewById(R.id.camera_button);

        mCameraButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View view)
    {
        if (view.getId()==R.id.camera_button){
            dispatchTakePicturesIntent();
            galleryAddPics();
        }
    }

    private void dispatchTakePicturesIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {

            }
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
       {
            mImageView.setImageURI(mCurrentPhotoUri);
       }
    }
    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoUri = Uri.fromFile(image);
        return image;
    }
    private void galleryAddPics()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mCurrentPhotoUri);
        this.sendBroadcast(mediaScanIntent);

    }


}
