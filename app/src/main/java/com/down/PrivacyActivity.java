package com.down;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class PrivacyActivity extends AppCompatActivity {

    private TextView policy;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacypolicy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Privacy Policy");
        policy = findViewById(R.id.policy);
        policy.setMovementMethod(new ScrollingMovementMethod());
        try {
            Resources res = getResources(); InputStream in_s = res.openRawResource(R.raw.privacypolicy);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            policy.setText(new String(b));
        } catch (Exception e) {
            policy.setText("Privacy Policy Not Available\n" + "See at: \n" + getString(R.id.policy));
        }
    }
}
