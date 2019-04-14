package com.example.down;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.INVISIBLE;

public class CreateDownActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<FriendEntry> friends;
    public ArrayList<FriendEntry> groups;
    private TextView noFriendsDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_down);
        this.setTitle(R.string.title_add_down);

        recyclerView = (RecyclerView) findViewById(R.id.rv_chooseFriendsList);
        noFriendsDisplay = (TextView) findViewById(R.id.noFriendsTextView_createDown);
        noFriendsDisplay.setText(R.string.emptyFriends);
        noFriendsDisplay.setVisibility(INVISIBLE); // assume they have friends

        // use a linear layout
        layoutManager = new LinearLayoutManager(CreateDownActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        // initializing database
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        // getting the user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // initializing the data
        this.friends = new ArrayList<>();
        this.groups = new ArrayList<>();
        // should we treat groups separately? CLD
        //this.groups = new ArrayList<>();

        // give data to the adapter and create the adapter
        // passing context to get access to resources files
        mAdapter = new ChooseFriendRecyclerViewAdapter(this, this.friends, this.groups);

        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Going through Friends and getting all the friends
                // that have been selected
                Log.d(TAG, "Printing friends");
                for (FriendEntry f: friends) {
                    if (f.getSelect()) {
                        Log.d(TAG, "Friend Selected: " + f.toString());
                    }
                }
                Intent intent = new Intent(view.getContext(), MyFeedActivity.class);
                startActivity(intent);
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                friends.clear();
                //groups.clear();
                Map<String, Integer> friendMap = (Map) dataSnapshot.child(user.getUid())
                        .child("friends").getValue();
                //Map<String, Integer> requestMap = (Map) dataSnapshot.child(user.getUid())
                        //.child("requests").getValue();

                // if the user doesn't have a friend section or any friends
                if (friendMap == null){
                    // if no friends display the no friends message
                    recyclerView.setVisibility(View.INVISIBLE);
                    noFriendsDisplay.setVisibility(View.VISIBLE);
                    return;
                }

                if (friendMap != null) {
                    Object[] friendUIDS = friendMap.keySet().toArray();
                    for (Object i : friendUIDS) {
                        String friendUID = (String) i;
                        String friendName = dataSnapshot.child(friendUID)
                                .child("name").getValue(String.class);
                        int friendAvatar = dataSnapshot.child(friendUID)
                                .child("avatar").getValue(Integer.class);
                        friends.add(new FriendEntry(friendName, friendUID, friendAvatar));
                    }
                }

                /*if (requestMap != null) {
                    Object[] requestUIDS = requestMap.keySet().toArray();
                    for (Object i : requestUIDS) {
                        String requestUID = (String) i;
                        String friendName = dataSnapshot.child(requestUID)
                                .child("name").getValue(String.class);
                        int friendAvatar = dataSnapshot.child(requestUID)
                                .child("avatar").getValue(Integer.class);
                        requests.add(new FriendEntry(friendName, requestUID, friendAvatar));
                    }
                }*/

                // sorting the friends for the display
                // uses that friends implements Comparable
                Collections.sort(friends);
                //Collections.sort(requests);

                Log.d(TAG, "Importing Friends...");
                Log.d(TAG, "Imported: " + friends.size() + " friends");
                //Log.d(TAG, "Importing Requests...");
                //Log.d(TAG, "Imported: " + requests.size() + " friends");
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
