package com.down;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        if (!sharedPreferences.getBoolean("Completed_Tutorial", false)) {
            // The user hasn't seen the OnboardingFragment yet, so show it
            Intent intent = new Intent(WelcomeActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }
    }
}
