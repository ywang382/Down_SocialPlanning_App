package com.example.down;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{
    private CardView name;
    private CardView password;
    private CardView avatar;
    private CardView options;
    private Switch notif;
    private CardView tutorial;
    private CardView credit;
    private DatabaseReference db;
    private String curUser;
    private FirebaseUser user;
    private int selected;
    private SharedPreferences preferences;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(R.string.action_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        db = FirebaseDatabase.getInstance().getReference("users");
        curUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();
        name = (CardView) findViewById(R.id.card_name);
        password = (CardView) findViewById(R.id.card_pw);
        avatar = (CardView) findViewById(R.id.card_avatar);
        notif = (Switch) findViewById(R.id.switch1);
        notif.setChecked(preferences.getBoolean("notif_on_off", false));
        options = (CardView) findViewById(R.id.card_notif_settings);
        tutorial = (CardView) findViewById(R.id.card_tutorial);
        credit = (CardView) findViewById(R.id.card_credit);

        name.setOnClickListener(this);
        password.setOnClickListener(this);
        avatar.setOnClickListener(this);
        tutorial.setOnClickListener(this);
        credit.setOnClickListener(this);
        options.setOnClickListener(this);

        notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean("notif_on_off", isChecked).commit();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == name){
            nameDialog();
        } else if(v == password){
            passwordDialog();
        } else if(v == avatar){
            avatarDialog();
        } else if(v == tutorial){
            Intent i = new Intent(SettingsActivity.this, TutorialPageActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else if(v == options){
            Intent i = new Intent(SettingsActivity.this, NotificationsActivity.class);
            startActivity(i);
        }

    }

    public void nameDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_setting_name);
        final EditText newText = dialog.findViewById(R.id.editText1);
        final EditText confirmText = dialog.findViewById(R.id.editText2);
        final Button confirm = dialog.findViewById(R.id.button);
        final Button cancel = dialog.findViewById(R.id.button2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newText.getText().toString().length() > 30){
                    newText.setError(getString(R.string.error_invalid_name));
                    return;
                }
                if(!newText.getText().toString().equals(confirmText.getText().toString())){
                    confirmText.setError("New name does not match.");
                    return;
                }
                db.child(curUser).child("name").setValue(newText.getText().toString());
                dialog.dismiss();
                Toast.makeText(SettingsActivity.this, getString(R.string.confirm_update), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void passwordDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_setting_password);
        final EditText oldPW = dialog.findViewById(R.id.editText1);
        final EditText newPW = dialog.findViewById(R.id.editText2);
        final EditText conPW = dialog.findViewById(R.id.editText3);
        final Button confirm = dialog.findViewById(R.id.button);
        final Button cancel = dialog.findViewById(R.id.button2);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pw = newPW.getText().toString();
                if(pw.isEmpty() || TextUtils.isDigitsOnly(pw) || pw.length() < 8){
                    newPW.setError(getString(R.string.error_invalid_password));
                    return;
                }
                if(!pw.equals(conPW.getText().toString())){
                    conPW.setError(getString(R.string.error_password_mismatch));
                    return;
                }
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), oldPW.getText().toString());
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(pw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            } else {
                                                newPW.setError("Password update unsuccessful, please try again");
                                            }
                                        }
                                    });
                                } else {
                                    oldPW.setError("Incorrect current password");
                                    oldPW.getText().clear();
                                    newPW.getText().clear();
                                    conPW.getText().clear();
                                }
                            }
                        });
            }
        });
        dialog.show();
    }

    public void avatarDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_setting_avatar);
        dialog.show();
        Spinner spinner = dialog.findViewById(R.id.spinner);
        final Button confirm = dialog.findViewById(R.id.button);
        final Button cancel = dialog.findViewById(R.id.button2);
        final TypedArray avatars = getResources().obtainTypedArray(R.array.avatar_imgs);
        AvatarSpinnerAdapter adapter = new AvatarSpinnerAdapter(this, avatars);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.child(curUser).child("avatar").setValue(selected);
                Toast.makeText(SettingsActivity.this, getString(R.string.confirm_update), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }


}
