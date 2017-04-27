package com.deshpande.camerademo;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by achou on 4/26/2017.
 */

public class CallServer extends AsyncTask<String, String, String>
{
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
    private static final String IMGUR_CLIENT_ID = "http://54.183.245.169:8080/upload";
    private String response = "";
    private WeakReference<Activity> mWeakActivity;

//    public CallServer(Activity act)
//    {
//        mWeakActivity = new WeakReference<Activity>(act);
//    }

    @Override
    protected String doInBackground(String... strings)
    {
        String filePath = strings[0];
        Log.d("HMKCODE", "[CallServer][doInBackground]filePath : "+filePath);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filePath,
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filePath)))
                .build();

        Request request = new Request.Builder()
                                        .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                                        //.url("https://api.imgur.com/3/image")
                                        .url(IMGUR_CLIENT_ID)
                                        .post(requestBody)
                                        .build();
        try
        {
            Log.d("HMKCODE", "Before Executing Request");
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            Log.d("HMKCODE", "[CallServer][doInBackground]RESPONSE : "+response.body().string());
            this.response = response.body().string();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return this.response;
    }

//    protected void onPostExecute(String response)
//    {
//        Activity main_activity = mWeakActivity.get();
//
//        if(main_activity != null)
//        {
//            Log.d("HMKCODE", "[CallServer][onPostExecute]RESPONSE : "+response);
//            TextView responseTextView = (TextView) main_activity.findViewById(R.id.responseTextView);
//            responseTextView.setText(response);
//
//        }
//    }
}
