package com.example.down;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText display_name;
    private EditText password;
    private EditText email;
    private EditText password_confirm;

    private FirebaseAuth mAuth;
    private DatabaseReference db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        display_name = findViewById(R.id.display_name);
        password_confirm = findViewById(R.id.password_confirm);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        update(curUser);
    }

    @Override
    public void onClick(View v){
        createAccount(email.getText().toString(), password.getText().toString());
    }

    private void createAccount(String em, String pw){
        if(!valid()) return;

        mAuth.createUserWithEmailAndPassword(em, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String name = display_name.getText().toString();
                            db.child(mAuth.getCurrentUser().getUid()).child("name").setValue(name);
                            update(mAuth.getCurrentUser());
                        } else{
                            email.setError(getString(R.string.error_used_email));
                        }
                    }
                });

    }


    private void update(FirebaseUser user){
        if(user != null){
            Toast.makeText(this, "You are now logged in with " + user.getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, MyFeedActivity.class);
            startActivity(intent);
        }
    }

    private boolean valid(){
        boolean flag = true;
        String nm = display_name.getText().toString();
        if(nm.length() < 2){
            display_name.setError(getString(R.string.error_invalid_name));
            flag = false;
        }
        String em = email.getText().toString();
        if(TextUtils.isEmpty(em) || !em.contains("@") || !em.contains(".")){
            email.setError(getString(R.string.error_invalid_email));
            flag = false;
        }
        String pw1 = password.getText().toString();
        String pw2 = password_confirm.getText().toString();
        if(TextUtils.isEmpty(pw1) || TextUtils.isDigitsOnly(pw1) || pw1.length() < 8){
            password.setError(getString(R.string.error_invalid_password));
            flag = false;
        }
        if(!pw1.equals(pw2)){
            password.setError(getString(R.string.error_password_mismatch));
            flag = false;
        }
        return flag;
    }
}

