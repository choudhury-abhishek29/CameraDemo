package com.visonus.camerademo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.deshpande.camerademo.R;

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
    private String response;
    private TaskCompleted mCallBack;
    private Context mContext;
//    private WeakReference<Activity> mWeakActivity;

    public CallServer(Context context)
    {
        this.mContext = context;
        this.mCallBack = (TaskCompleted) context;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String filePath = strings[0];
        String text="";
        Log.d("HMKCODE", "[CallServer][doInBackground]filePath : "+filePath);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filePath,
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filePath)))
                .build();

        Request request = new Request.Builder()
                                        .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                                        .url(IMGUR_CLIENT_ID)
                                        .post(requestBody)
                                        .build();
        try
        {
            Log.d("HMKCODE", "Before Executing Request");
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            text = response.body().string();
            Log.d("HMKCODE", "[CallServer][doInBackground]RESPONSE : "+text);
            this.response = text;

//            return response.body().string();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }

    protected void onPostExecute(String response)
    {
        Log.d("HMKCODE", "[CallServer][onPostExecute]RESPONSE : "+response);
        mCallBack.onTaskComplete(this.response);
    }
}
