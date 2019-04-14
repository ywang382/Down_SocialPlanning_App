package com.example.down;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {

    SearchView searchBar;
    ListView searchFriends;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Friends");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");



        searchFriends = findViewById(R.id.friendsearch);
        ArrayList<String> friendsArray = new ArrayList<>();
        friendsArray.addAll(Arrays.asList(getResources().getStringArray(R.array.friendsArray)));

        adapter = new ArrayAdapter<String>(
                AddFriendActivity.this,
                android.R.layout.simple_list_item_1,
                friendsArray
        );

        searchFriends.setAdapter(adapter);

        searchBar = findViewById(R.id.searchBar);
        //searchBar.setIconified(false);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

    }

}