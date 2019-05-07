package com.down;

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


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText email;
    private EditText password;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.register_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        update(curUser);
    }

    private void update(FirebaseUser user){
        if(user != null) {
            Toast.makeText(this, "You are now logged in with " + user.getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MyFeedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        if(id == R.id.register_button){
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if(id == R.id.sign_in_button){
            signIn(email.getText().toString(), password.getText().toString());
        }
    }

    private void signIn(String em, String pw){
        if(!valid()) return;
        mAuth.signInWithEmailAndPassword(em, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            update(mAuth.getCurrentUser());
                        } else{
                            Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                            email.setText("");
                            password.setText("");
                        }
                    }
                });

    }

    private boolean valid(){
        String em = email.getText().toString();
        if(TextUtils.isEmpty(em) || !em.contains("@") || !em.contains(".")){
            email.setError(getString(R.string.error_invalid_email));
            return false;
        }
        String pw = password.getText().toString();
        if(TextUtils.isEmpty(pw)){
            password.setError(getString(R.string.error_invalid_password));
            return false;
        }
        return true;
    }
}

