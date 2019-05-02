package com.example.down;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TutorialPageActivity extends AppCompatActivity {
    private FloatingActionButton next;
    private FloatingActionButton prev;
    private ImageView image;
    private TextView note;
    private TypedArray texts;
    private TypedArray images;
    private int pageCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.tutorial_page);
        getSupportActionBar().hide();
        image = (ImageView) findViewById(R.id.tutorial_image);
        note = (TextView) findViewById(R.id.tutorial_text);
        next = (FloatingActionButton) findViewById(R.id.fabNext);
        prev = (FloatingActionButton) findViewById(R.id.fabPrev);
        texts = getResources().obtainTypedArray(R.array.tutorial_text);
        images = getResources().obtainTypedArray(R.array.tutorial_imgs);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageCounter++;
                if(pageCounter >= 3){
                    Intent intent = new Intent(TutorialPageActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    sharedPreferences.edit().putBoolean("Completed_Tutorial", true).commit();
                } else{
                    image.setImageDrawable(images.getDrawable(pageCounter));
                    note.setText(texts.getString(pageCounter));
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageCounter--;
                if (pageCounter <= -1) {
                    Intent intent = new Intent(TutorialPageActivity.this, TutorialActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    image.setImageDrawable(images.getDrawable(pageCounter));
                    note.setText(texts.getString(pageCounter));
                }
            }
        });

    }
}
