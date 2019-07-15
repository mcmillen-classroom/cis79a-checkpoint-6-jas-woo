package com.example.cameraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{

    private ImageView mImageView;
    private Button mCameraButton;
    private Button mShareButton;
    private Button mEmailButton;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    Uri mCurrentPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.image_view);
        mCameraButton = (Button) findViewById(R.id.camera_button);
        mShareButton = (Button) findViewById(R.id.share_button);
        mEmailButton = (Button) findViewById(R.id.email_button);

        mCameraButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        mEmailButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.camera_button)
        {
            dispatchTakePicturesIntent();
            galleryAddPics();
        }
        else if (view.getId() == R.id.share_button)
        {
            dispatchSharePicture();
        }
        else
        {
            dispatchEmailPicture();
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
    private void galleryAddPics()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mCurrentPhotoUri);
        this.sendBroadcast(mediaScanIntent);

    }
    private void dispatchSharePicture()
    {
        File image = new File(mCurrentPhotoUri.getPath());
        Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", image);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
        shareIntent.setType("image/jpeg");

        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_pic)));
    }
    private void dispatchEmailPicture()
    {
        File image = new File(mCurrentPhotoUri.getPath());
        Uri photoUri = FileProvider.getUriForFile(this,"com.example.android.fileprovider", image);

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Checks this pic't I just took't!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "ALL PICS TOOK'T BY jas woo CAMERA APP, shorty!");
        emailIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        emailIntent.setType("text/plain");

        if (emailIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(emailIntent);
        }
        else
        {
            Toast.makeText(this, "I'm sure you figured that there is no email app configured, in case you didn't, now you knows.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
       {
            setPic();
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
    private void setPic()
    {
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoUri.getPath(), bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

}
