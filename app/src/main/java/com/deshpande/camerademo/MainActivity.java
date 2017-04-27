package com.deshpande.camerademo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
//import android.os.AsyncTask;
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

//import java.io.BufferedOutputStream;
import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button takePictureButton;
    private ImageView imageView;
    private TextView textView;
//    private TextView responseTextView;
    private Uri file;


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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
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
        File f = new File(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp + ".jpg");
        return f;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("HMKCODE", "[onActivityResult]");
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
                Log.d("HMKCODE", "File Path : "+file.getPath());
                textView.setText("PATH : "+file.getPath());
                try
                {
                    Log.d("HMKCODE", "Calling [connectForMultipart]");
                    connectForMultipart(file.getPath());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connectForMultipart(String filePath) throws Exception
    {
        Log.d("HMKCODE", "[connectForMultipart]");
//        CallServer c = new CallServer(this);
        CallServer c = new CallServer();
        c.execute(filePath);

    }
}
