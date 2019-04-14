package com.example.down;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;


public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<DownEntry> downs;
    private TextView noDownsDisplay;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_feed, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.title_my_feed);
        recyclerView = (RecyclerView) this.getView().findViewById(R.id.rvDowns);
        noDownsDisplay = (TextView) this.getView().findViewById(R.id.noDownsTextView);
        noDownsDisplay.setText(R.string.emptyDowns);
        noDownsDisplay.setVisibility(view.INVISIBLE); // assume they have downs

        // use a linear layout
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        // initializing database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        // getting the user
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // initializing the data
        this.downs = new ArrayList<>();
        // give data to the adapter and create the adapter
        // passing context to get access to resources files
        mAdapter = new DownAdapter(this.getContext(), downs);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = this.getView().findViewById(R.id.fabDown);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddDownActivity.class);
                startActivity(intent);
            }
        });


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                downs.clear();
                ArrayList<String> downIDs = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.child("users").child(uid).child("downs").getChildren()) {
                    String dID = d.getKey();
                    if(!dataSnapshot.child("down").hasChild(dID)){
                        d.getRef().removeValue(); // Delete null references
                    } else {
                        downIDs.add(dID);
                    }
                }

                if(downIDs.isEmpty()){
                    recyclerView.setVisibility(View.INVISIBLE);
                    noDownsDisplay.setVisibility(View.VISIBLE);
                }

                for(String id : downIDs){
                    long timestamp = dataSnapshot.child("down").child(id).child("timestamp").getValue(Long.class);
                    long cur = System.currentTimeMillis();
                    final long ONE_DAY = 86400000;
                    // Skip outdated downs
                    if(timestamp < cur - ONE_DAY){
                        continue;
                    }
                    DownEntry down = dataSnapshot.child("down").child(id).getValue(DownEntry.class);
                    int downStatus = dataSnapshot.child("down").child(id).child("invited").child(uid).getValue(Integer.class);
                    down.id = id;
                    down.creator = dataSnapshot.child("users").child(down.creator).child("name").getValue(String.class);
                    down.isDown = (downStatus == 1);
                    downs.add(down);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}