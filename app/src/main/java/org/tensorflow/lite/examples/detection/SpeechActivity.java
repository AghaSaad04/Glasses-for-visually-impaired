package org.tensorflow.lite.examples.detection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechActivity extends AppCompatActivity {
    private TextView textResult;
    private String OPERATOR;
    private ImageButton micButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        getSpeechInput();
        textResult= findViewById(R.id.textResult);
        micButton= findViewById(R.id.micButton);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpeechInput();
            }
        });
    }
    public void getSpeechInput(){
        Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, 10);
        }
        else {
            Toast.makeText(this, "Your Device Doesn't Support Speech Input", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                String operatorFound = getTextFromResult(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));
                if (operatorFound != null) {
                    OPERATOR = operatorFound;
                    textResult.setText(String.valueOf(operatorFound));
//              if(resultCode==RESULT_OK && data!=null){
//                  ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                  textResult.setText(result.get(0));
//              }
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, I didn't catch that! Please try again", Toast.LENGTH_LONG).show();


                }
        }
    }
    private String getTextFromResult(ArrayList<String> results) {
        for (String str : results) {
            if (switchModules(str) != null) {
                return switchModules(str);
            }
        }
        return null;
    }
    private String switchModules(String strOper) {
        switch (strOper) {
            case "open book reading":
            case "open object character recognition":
                OCR();
                return "OCR is open";
            case "open object detection":
            case "open direction":
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int ttsLang = textToSpeech.setLanguage(Locale.US);
                            String data = "Opening Object detection";
                            tts(data);
                            if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                                    || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "The Language is not supported!");
                            } else {
                                Log.i("TTS", "Language Supported.");
                            }
                            Log.i("TTS", "Initialization success.");
                        } else {
                            Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                objectDetection();
                return "Object detection is open";
        }
        return null;
    }

    public void tts(String label){

        //                String data = editText.getText().toString();
        //                Log.i("TTS", "button clicked: " + data);
        int speechStatus = textToSpeech.speak(label, TextToSpeech.QUEUE_FLUSH, null);

        if (speechStatus == TextToSpeech.ERROR) {
            Log.e("TTS", "Error in converting Text to Speech!");
        }
    }

    private void OCR(){
        System.out.println("OCR");
    }
    private void objectDetection(){
        startActivity(new Intent(SpeechActivity.this, DetectorActivity.class));
        System.out.println("Object detection is open");

    }
}