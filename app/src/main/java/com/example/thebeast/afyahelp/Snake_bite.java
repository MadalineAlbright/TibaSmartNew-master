package com.example.thebeast.afyahelp;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.Locale;

public class Snake_bite extends AppCompatActivity implements TextToSpeech.OnInitListener{
    ImageView imageView;
    TextToSpeech engine;
    float pitchRate=1f,speedRate=0.8f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_bite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_myaccount);
        setSupportActionBar(toolbar);


        imageView=findViewById(R.id.youtube_video);

        engine=new TextToSpeech(this,this);//initiallizing the TTS engine



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent( Snake_bite.this, YoutubeApiKey_Holder.getApiKey(),"rjP1FaOCizQ",100,true,true);
                startActivity(intent);
            }
        });


        ImageView back_btn=findViewById(R.id.id_backmain);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in=new Intent(getApplicationContext(),Bites_problem.class);
                startActivity(in);

            }
        });



        FloatingActionButton floatingActionButton=findViewById(R.id.floatingActionButton2);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in=new Intent(getApplicationContext(),EmergencyLines.class);
                startActivity(in);

            }
        });
    }

    @Override
    public void onInit(int status) {

        //It hosts the tts engine
        if(status==TextToSpeech.SUCCESS){

            engine.setLanguage(Locale.UK);
        }

    }




    private void speak() {
        engine.setPitch(pitchRate);
        engine.setSpeechRate(speedRate);

        String about=getResources().getString(R.string.title_about);
        String about_description=getResources().getString(R.string.bites_about_content);

        String recognition=getResources().getString(R.string.bites_priority);
        String recognition_signs=getResources().getString(R.string.bites_priority_content);

        String type=getResources().getString(R.string.title_treatment);
        String type_description=getResources().getString(R.string.bites_treatment_content);


        engine.speak(about+".\n"+about_description+".\n"+type+".\n"+type_description
                        +".,\n"+recognition+".\n"+recognition_signs,
                TextToSpeech.QUEUE_ADD,null,null);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.steps_menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.start_tts) {
            Toast.makeText(Snake_bite.this,"Read the app contents",Toast.LENGTH_LONG).show();
            speak();
            return true;
        }
        else if (id == R.id.stop_tts) {
            Toast.makeText(Snake_bite.this,"Stop reading the app contents",Toast.LENGTH_LONG).show();
            engine.stop();
            return true;
        }

        else {
            return super.onOptionsItemSelected(item);}
    }

}
