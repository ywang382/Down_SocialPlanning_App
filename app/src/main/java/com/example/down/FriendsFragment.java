package com.example.down;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;


public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<FriendEntry> friends;
    public ArrayList<FriendEntry> requests;
    private TextView noFriendsDisplay;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_friends, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setting the title to the new page
        getActivity().setTitle(R.string.title_fragment_my_friends);
        recyclerView = (RecyclerView) this.getView().findViewById(R.id.rv_friendsList);
        noFriendsDisplay = (TextView) this.getView().findViewById(R.id.noFriendsTextView);
        noFriendsDisplay.setText(R.string.emptyFriends);
        noFriendsDisplay.setVisibility(view.INVISIBLE); // assume they have friends

        // use a linear layout
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // initializing database
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        // getting the user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // initializing the data
        this.friends = new ArrayList<>();
        this.requests = new ArrayList<>();

        // give data to the adapter and create the adapter
        // passing context to get access to resources files
        mAdapter = new FriendRecyclerViewAdapter(this.getContext(), this.friends, this.requests,
                new ClickListener() { // adding a listener for what to do when clicked
                    @Override
                    public void onPositionClicked(int position, boolean accepted) {
                        if (position < requests.size()){ // ensuring accessing one of the first values
                            String otherUID = requests.get(position).getUid();
                            if (accepted) {
                                // if they accepted, should add friend to user and friend account
                                // ... and remove request from list
                                //db.child(user.getUid()).child("friends").child(otherUID).removeValue();
                                db.child(user.getUid()).child("friends").child(otherUID).setValue(0);
                                db.child(otherUID).child("friends").child(user.getUid()).setValue(0);
                                db.child(user.getUid()).child("requests").child(otherUID).removeValue();
                                requests.remove(position);
                            } else {
                                // if they rejected, should remove friend from requests on page
                                db.child(user.getUid()).child("requests").child(otherUID).removeValue();
                                requests.remove(position);
                            }
                        } else {
                            // they clicked ona friend, should not be possible
                        }
                        // callback performed on click
                    }
                });

        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = this.getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                startActivity(intent);
            }
        });

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                friends.clear();
                requests.clear();
                Map<String, Integer> friendMap = (Map) dataSnapshot.child(user.getUid())
                        .child("friends").getValue();
                Map<String, Integer> requestMap = (Map) dataSnapshot.child(user.getUid())
                        .child("requests").getValue();

                // if the user doesn't have a friend section or any friends
                if (friendMap == null && requestMap == null){
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

                if (requestMap != null) {
                    Object[] requestUIDS = requestMap.keySet().toArray();
                    for (Object i : requestUIDS) {
                        String requestUID = (String) i;
                        String friendName = dataSnapshot.child(requestUID)
                                .child("name").getValue(String.class);
                        int friendAvatar = dataSnapshot.child(requestUID)
                                .child("avatar").getValue(Integer.class);
                        requests.add(new FriendEntry(friendName, requestUID, friendAvatar));
                    }
                }

                // sorting the friends for the display
                // uses that friends implements Comparable
                Collections.sort(friends);
                Collections.sort(requests);

                Log.d(TAG, "Importing Friends...");
                Log.d(TAG, "Imported: " + friends.size() + " friends");
                Log.d(TAG, "Importing Requests...");
                Log.d(TAG, "Imported: " + requests.size() + " friends");
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
