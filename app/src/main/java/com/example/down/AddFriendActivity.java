package com.example.down;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class AddFriendActivity extends AppCompatActivity {

    ListView friendSearch;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Friends");

        friendSearch = findViewById(R.id.friendsearch);

        ArrayList<String> arrayFriends = new ArrayList<>();
        arrayFriends.addAll(Arrays.asList(getResources().getStringArray(R.array.myFriends)));

        adapter = new ArrayAdapter<String>(
                AddFriendActivity.this,
                android.R.layout.simple_list_item_1,
                arrayFriends
        );

        friendSearch.setAdapter(adapter);
    }

}
