package com.example.down;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class DownStatusActivity extends AppCompatActivity{
    private TextView userStatus;
    private TextView userName;
    private ImageView userAvatar;
    private ImageView delete;
    private TypedArray avatars;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<String> users;
    private String downID;
    private String curUser;
    public DatabaseReference db;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) findViewById(R.id.title);
        TextView timetext = (TextView) findViewById(R.id.time_and_person);
        userAvatar = (ImageView) findViewById(R.id.user_avatar);
        userName = (TextView) findViewById(R.id.user_name);
        avatars = getResources().obtainTypedArray(R.array.avatar_imgs);
        userStatus = (TextView) findViewById(R.id.user_status);
        delete = (ImageView) findViewById(R.id.delete_down);
        curUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlert();
            }
        });

        setTitle(R.string.title_down_status);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle extra = getIntent().getExtras();
        title.setText(extra.getString("title"));
        timetext.setText(extra.getString("timetext"));
        downID = extra.getString("downID");
        Log.d("tim", extra.getString("creator") + " " + curUser);
        if(!extra.getString("creator").equals(curUser)){
            delete.setVisibility(View.INVISIBLE);
        }


        db = FirebaseDatabase.getInstance().getReference();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                userAvatar.setImageDrawable(avatars.getDrawable(ds.child("users").child(curUser).child("avatar").getValue(Integer.class)));
                String st = ds.child("down").child(downID).child("status").child(curUser).getValue(String.class);
                if(st != null) {
                    userStatus.setText((st.isEmpty()) ? "Click to add a status..." : st);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog();
            }
        });
        userStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog();
            }
        });

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.rvStatus);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        users = new ArrayList<>();
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                ArrayList<String> downIDs = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.child("down").child(downID).child("status").getChildren()) {
                    users.add(d.getKey());
                }

                Parcelable recyclerViewState;
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                mAdapter = new DownStatusAdapter(getApplicationContext(), users, downID);
                recyclerView.setAdapter(mAdapter);
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    public void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update your status");
        final EditText input = new EditText(this);
        input.setHint(userStatus.getText());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userStatus.setText(input.getText().toString());
                db.child("down").child(downID).child("status").child(curUser).setValue(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show().getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    public void buildAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_down_title);
        builder.setMessage(getString(R.string.delete_down_dialog));
        builder.setCancelable(false);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DownStatusActivity.this, MyFeedActivity.class);
                startActivity(intent);
                finish();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("down");
                db.child(downID).removeValue();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
    }
}
