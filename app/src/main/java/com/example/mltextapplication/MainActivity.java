package com.example.mltextapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    LinearLayout copy,clear,camera;
    Uri imageUrl;
    TextRecognizer textRecognizer;
    TextToSpeech textToSpeech;
    Button readButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.cameraLl);
        copy = findViewById(R.id.copyLl);
        clear = findViewById(R.id.clearLl);
        readButton = findViewById(R.id.textToSpeech);

        editText = findViewById(R.id.textSpace);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                if (text.isEmpty()){
                    textToSpeech.speak("There is no text to read",TextToSpeech.QUEUE_FLUSH,null);
                }
                else {
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                }

            }
        });



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();


            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                if (text.isEmpty()){
                    Toast.makeText(MainActivity.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
                }
                else{
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Data",editText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Text copy in Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString().trim();
                if (text.isEmpty()){
                    Toast.makeText(MainActivity.this, "There is no text to clear", Toast.LENGTH_SHORT).show();
                }
                else{
                    editText.setText("");
                }
            }
        });


    }

    private void recognizeText(){
        if (imageUrl!=null){
            try {
                InputImage inputImage = InputImage.fromFilePath(MainActivity.this,imageUrl);

                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                String recognizeText = text.getText();
                                editText.setText(recognizeText);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            catch (@NonNull Exception exception){
                exception.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){
            if (data!=null){
                imageUrl = data.getData();
                Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                recognizeText();
            }
        }
        else {
            Toast.makeText(this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }
}