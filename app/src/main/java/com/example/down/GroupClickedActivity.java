package com.example.down;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class GroupClickedActivity extends AppCompatActivity {

    String groupName;
    ArrayList<String> groupUIDs;
    TextView nameOfGroup;

    EditText search_edit_text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    ArrayList<Integer> avatarList;
    ArrayList<String> UIDList;
    SearchAdapterInGroup searchAdapter;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_clicked);
        nameOfGroup = (TextView) findViewById(R.id.group_name);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_activity_group));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        groupName = extras.getString("GROUP_NAME");
        groupUIDs = extras.getStringArrayList("GROUP_UIDS");

        nameOfGroup.setText(groupName);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        search_edit_text = (EditText) findViewById(R.id.search_edit_text);



        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UID = firebaseUser.getUid();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //Creates a array list for each node
        nameList = new ArrayList<>();
        emailList = new ArrayList<>();
        avatarList = new ArrayList<>();
        UIDList = new ArrayList<>();

        //Sets adapter to display friends options
        setAdapter("");

        //Method to change what friends are displayed with change in text
        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());
                } else {
                    /*
                     * Clear the list when editText is empty
                     * */
                    nameList.clear();
                    emailList.clear();
                    avatarList.clear();
                    UIDList.clear();
                    recyclerView.removeAllViews();
                    setAdapter(" ");
                }
            }
        });



    }
    private void setAdapter(final String searchedString) {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                nameList.clear();
                emailList.clear();
                avatarList.clear();
                UIDList.clear();
                recyclerView.removeAllViews();

                int counter = 0;

                if (searchedString.length() == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String UID = snapshot.getKey();
                        Integer avatarIndex = snapshot.child("avatar").getValue(Integer.class);
                        if (groupUIDs.contains(UID)) {
                            nameList.add(name);
                            emailList.add(email);
                            UIDList.add(UID);
                            avatarList.add(avatarIndex);
                            counter++;
                        }

                        if (counter == 15)
                            break;
                    }

                } else {

                    String curUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    /*
                     * Search all users for matching searched string
                     * */
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("friends").hasChild(curUser)) {
                            continue;
                        }
                        if(snapshot.getKey().equals(curUser)){
                            continue;
                        }

                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String UID = snapshot.getKey();
                        Integer avatarIndex = snapshot.child("avatar").getValue(Integer.class);

                        if (name.toLowerCase().contains(searchedString.toLowerCase())) {
                            if (groupUIDs.contains(UID)) {
                                nameList.add(name);
                                emailList.add(email);
                                UIDList.add(UID);
                                avatarList.add(avatarIndex);
                                counter++;
                            }
                        } else if (email.toLowerCase().contains(searchedString.toLowerCase())) {
                            if (groupUIDs.contains(UID)) {
                                nameList.add(name);
                                emailList.add(email);
                                UIDList.add(UID);
                                avatarList.add(avatarIndex);
                                counter++;
                            }
                        }

                        /*
                         * Get maximum of 15 searched results only
                         * */
                        if (counter == 15)
                            break;
                    }
                }

                searchAdapter = new SearchAdapterInGroup(GroupClickedActivity.this, nameList, emailList, avatarList, UIDList, UID);

                //SearchAdapter(AddFriendActivity.this, nameList, emailList);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}