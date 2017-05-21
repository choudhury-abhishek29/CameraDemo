package com.visonus.camerademo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.deshpande.camerademo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements TaskCompleted {

    private Button takePictureButton;
    private ImageView imageView;
    private TextView textView;
    private TextView responseTextView;
    private Uri file;
    private Bitmap photo;
    private String path;
    public static final String RESPONSE_TEXT = "com.visonus.camerademo.RESPONSE_TEXT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("HMKCODE", "[onCreate]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageview);
        textView = (TextView) findViewById(R.id.textView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("HMKCODE", "[onRequestPermissionsResult]");
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Log.d("HMKCODE", "[takePicture]");
        Intent saveIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        saveIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(saveIntent, 100);

    }

    public void performCrop(Uri crop)
    {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(crop, "image/*");
        cropIntent.putExtra("return-data", true);
//        cropIntent.putExtra("outputX", 3264);
//        cropIntent.putExtra("outputY", 1836);
//        cropIntent.putExtra("aspectX", 1);
//        cropIntent.putExtra("aspectY", 1);
//        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, crop);
        startActivityForResult(cropIntent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("HMKCODE", "[onActivityResult]");
        if (resultCode == RESULT_OK) {

            switch(requestCode)
            {
                case 100:
                    path = file.getPath();
                    performCrop(file);
                    break;

                case 200:
                    Bundle extras = data.getExtras();
                    photo = extras.getParcelable("data");
                    Log.d("HMKCODE", "[onActivityResult]Intent DATA : "+data.toString());
                    FileOutputStream fileOutputStream = null;
                    try
                    {
                        fileOutputStream = new FileOutputStream(path);
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fileOutputStream != null) {
                                fileOutputStream.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    imageView.setImageBitmap(photo);
                    textView.setText("PATH : "+path);

                    try
                    {
                        Log.d("HMKCODE", "Calling connectForMultipart");
                        connectForMultipart(path);
                        Log.d("HMKCODE", "[MainActivity][onActivityResult]post text read");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        }
    }

    public File getOutputMediaFile(){
        Log.d("HMKCODE", "[getOutputMediaFile]");
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File f = new File(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpeg");
        return f;
    }

    public void connectForMultipart(String filePath) throws Exception
    {
        Log.d("HMKCODE", "[MainActivity][connectForMultipart]");
        new CallServer(MainActivity.this).execute(filePath);
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d("HMKCODE", "[MainActivity][onTaskComplete]RESPONSE : "+result);
        Intent readIntent = new Intent(this, ReadActivity.class);
        readIntent.putExtra(RESPONSE_TEXT, result);
        startActivity(readIntent);
    }
}
