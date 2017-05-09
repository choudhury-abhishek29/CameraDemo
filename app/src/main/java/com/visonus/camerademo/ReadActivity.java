package com.visonus.camerademo;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deshpande.camerademo.R;

import java.util.Locale;

public class ReadActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener{

    private EditText recievedText;
    private Button button_speak;
    private TextToSpeech tts;
    private String responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        this.recievedText = (EditText) findViewById(R.id.recievedText);
        this.button_speak = (Button) findViewById(R.id.button_speak);
        button_speak.setOnClickListener(this);
        tts = new TextToSpeech(this, this);

        Intent intent = getIntent();
        responseText = intent.getStringExtra(MainActivity.RESPONSE_TEXT);
        recievedText.setText(responseText);
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
                if(responseText.isEmpty()){
                    Toast.makeText(ReadActivity.this, "Text is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    tts.speak(responseText, TextToSpeech.QUEUE_FLUSH, null );
                }
                break;
        }

    }
}
