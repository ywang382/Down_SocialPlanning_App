package com.example.down;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {
    EditText search_edit_text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    //ArrayList<Long> avatarList;
    SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        search_edit_text = (EditText) findViewById(R.id.search_edit_text);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        /*
         * Create a array list for each node you want to use
         * */
        nameList = new ArrayList<>();
        emailList = new ArrayList<>();
        //avatarList = new ArrayList<>();

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
                    //avatarList.clear();
                    recyclerView.removeAllViews();
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
                //avatarList.clear();
                recyclerView.removeAllViews();

                int counter = 0;

                /*
                 * Search all users for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Long avatar = snapshot.child("avatar").getValue(Long.class);

                    if (name.toLowerCase().contains(searchedString.toLowerCase())) {
                        nameList.add(name);
                        emailList.add(email);
                        //avatarList.add(avatar);
                        counter++;
                    } else if (email.toLowerCase().contains(searchedString.toLowerCase())) {
                        nameList.add(name);
                        emailList.add(email);
                        //avatarList.add(avatar);
                        counter++;
                    }

                    /*
                     * Get maximum of 15 searched results only
                     * */
                    if (counter == 15)
                        break;
                }

                searchAdapter = new SearchAdapter(AddFriendActivity.this, nameList, emailList);

                        //SearchAdapter(AddFriendActivity.this, nameList, emailList);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}