package com.example.down;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddDownActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_down);
        this.setTitle(R.string.title_add_down);
    }
}
