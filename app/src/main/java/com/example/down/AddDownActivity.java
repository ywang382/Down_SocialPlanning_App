package com.example.down;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Time;

public class AddDownActivity extends AppCompatActivity {

    private EditText title;
    private TimePicker timePicker;
    private FloatingActionButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_down);
        //this.setTitle(R.string.title_add_down);

        title = (EditText) findViewById(R.id.down_title);
        timePicker = (TimePicker) findViewById(R.id.timePicker1);
        next = (FloatingActionButton) findViewById(R.id.fab_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String downTitle = title.getText().toString();
                if(downTitle.length() > 30 || downTitle.length() <= 5){
                    title.setError(getString(R.string.error_long_title));
                    return;
                }
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();
                Intent intent = new Intent(AddDownActivity.this, CreateDownActivity.class);
                intent.putExtra("title", downTitle);
                intent.putExtra("hour", hour);
                intent.putExtra("minute", min);
                startActivity(intent);
            }
        });

    }
}
