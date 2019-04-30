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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.INVISIBLE;

public class CreateDownActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<FriendEntry> friends;
    public ArrayList<GroupEntry> groups;
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

        Bundle extras = getIntent().getExtras();
        final int inputHour = extras.getInt("hour");
        final int inputMin = extras.getInt("minute");
        final String title = extras.getString("title");
        final long ONE_DAY = 86400000;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Going through Friends and getting all the friends
                // that have been invited
                ArrayList<String> invited = new ArrayList<>();
                for(FriendEntry f : friends){
                    if(f.getSelect())
                        invited.add(f.getUid());
                }

                if(invited.isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.error_no_select, Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis() + TimeZone.getTimeZone("EST5EDT")
                        .getOffset(System.currentTimeMillis()));
                cal.set(Calendar.HOUR_OF_DAY, inputHour);
                cal.set(Calendar.MINUTE, inputMin);
                long timestamp = cal.getTimeInMillis();
                if(timestamp < System.currentTimeMillis()){
                    timestamp += ONE_DAY;
                }
                int hour = (inputHour > 12) ? inputHour - 12 : inputHour;
                String time = hour + ":" + new DecimalFormat("00").format(inputMin) + " "
                        + ((inputHour >= 12) ? "PM" : "AM");
                String creator = user.getUid();

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("down");
                String downId = dbRef.push().getKey();
                dbRef = dbRef.child(downId);
                dbRef.child("time").setValue(time);
                dbRef.child("timestamp").setValue(timestamp);
                dbRef.child("nDown").setValue(1);
                dbRef.child("creator").setValue(creator);
                dbRef.child("status").child(creator).setValue("");
                dbRef.child("title").setValue(title);
                dbRef.child("nInvited").setValue(1 + invited.size());
                dbRef.child("invited").child(creator).setValue(1);
                db.child(creator).child("downs").child(downId).setValue(1);

                for (String userID : invited) {
                    dbRef.child("invited").child(userID).setValue(0);
                    db.child(userID).child("downs").child(downId).setValue(0);
                    dbRef.child("status").child(userID).setValue("");
                }
                Intent intent = new Intent(view.getContext(), MyFeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                Map<String, Integer> groupNamesMap = (Map) dataSnapshot.child(user.getUid())
                        .child("groups").getValue();

                // if the user doesn't have a friend section or any friends
                if (friendMap == null && groupNamesMap == null){
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


                if (groupNamesMap != null) {
                    Object[] groupNames = groupNamesMap.keySet().toArray();
                    for (Object i : groupNames) {
                        String groupName = (String) i;
                        Log.d(TAG, groupName);
                    }
                }
                /*
                        Map<String, Integer> group = (Map) dataSnapshot.child(user.getUid())
                                .child("groups").getValue();


                        String friendName = dataSnapshot.child(groupUID)
                                .child("name").getValue(String.class);
                        int friendAvatar = dataSnapshot.child(requestUID)
                                .child("avatar").getValue(Integer.class);
                        groups.add(new GroupEntry(friendName, requestUID, friendAvatar));
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
