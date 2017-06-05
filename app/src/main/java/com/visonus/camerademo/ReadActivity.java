package com.visonus.camerademo;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deshpande.camerademo.R;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener{

    private TextView recievedText;
    private Button button_speak, button_stop;
    private TextToSpeech tts;
    private String responseText="", imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        this.recievedText = (TextView) findViewById(R.id.recievedText);
        recievedText.setMovementMethod(new ScrollingMovementMethod());
        this.button_speak = (Button) findViewById(R.id.button_speak);
        this.button_stop = (Button) findViewById(R.id.button_stop);
        button_speak.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        tts = new TextToSpeech(this, this);

        Intent intent = getIntent();
        responseText = intent.getStringExtra(MainActivity.RESPONSE_TEXT);
        imageType = intent.getStringExtra(MainActivity.IMAGE_TYPE);

        if(imageType.equalsIgnoreCase("Code") && responseText!=null)
        {
            String pattern_url = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            if(responseText.matches(pattern_url))
            {
                recievedText .setText(responseText);
                Linkify.addLinks(recievedText , Linkify.WEB_URLS);
            }
            else
            {
                String pattern_num = "[0-9a-zA-Z]+";
                if(responseText.matches(pattern_num))
                {
                    Linkify.TransformFilter myTransformFilter = new Linkify.TransformFilter() {
                        @Override
                        public String transformUrl(Matcher match, String url) {
                            return url.substring(0);
                        }
                    };
                    Pattern pat_num = Pattern.compile(pattern_num);
                    recievedText.setText(responseText);
                    Linkify.addLinks(recievedText, pat_num, "http://www.google.com/search?q=", null, myTransformFilter);
                }
            }
        }
        else
        {
            recievedText.setText(responseText);
        }

    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS){
            Locale locale = tts.getLanguage();
            int result = tts.setLanguage(locale);
            if(result == TextToSpeech.LANG_MISSING_DATA || result== TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","This language is not supported");
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.button_speak:
                if(responseText.isEmpty())
                    Toast.makeText(ReadActivity.this, "Text is empty", Toast.LENGTH_SHORT).show();
                else
                    tts.speak(responseText, TextToSpeech.QUEUE_FLUSH, null );
                break;
            case R.id.button_stop:
                tts.stop();
                break;
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        tts.stop();
    }
}
