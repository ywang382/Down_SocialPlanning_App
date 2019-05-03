package com.example.down;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationsActivity extends AppCompatActivity{
    private Switch down;
    private Switch status;
    private Switch request;
    private Switch downdelete;
    private SharedPreferences preferences;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().setTitle(R.string.notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        down = (Switch) findViewById(R.id.switch1);
        down.setChecked(preferences.getBoolean("notif_down", false));
        request = (Switch) findViewById(R.id.switch2);
        request.setChecked(preferences.getBoolean("notif_request", false));
        status = (Switch) findViewById(R.id.switch3);
        status.setChecked(preferences.getBoolean("notif_status", false));
        downdelete = (Switch) findViewById(R.id.switch4);
        downdelete.setChecked(preferences.getBoolean("notif_downdelete", false));

        down.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("notif_down", isChecked).commit();
            }
        });
        request.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("notif_request", isChecked).commit();
            }
        });
        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("notif_status", isChecked).commit();
            }
        });
        downdelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("notif_downdelete", isChecked).commit();
            }
        });
    }


}
