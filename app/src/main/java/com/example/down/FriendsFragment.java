package com.example.down;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
    private TextView noFriendsDisplay;
    //private FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_friends, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // setting the title to the new page
        getActivity().setTitle(R.string.title_activity_my_friends);
        recyclerView = (RecyclerView) this.getView().findViewById(R.id.rv_friendsList);
        noFriendsDisplay = (TextView) this.getView().findViewById(R.id.noFriendsTextView);
        noFriendsDisplay.setText(R.string.emptyFriends);
        noFriendsDisplay.setVisibility(view.INVISIBLE); // assume they have friends

        // use a linear layout
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // initializing database
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        // getting the user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // initializing the data
        this.friends = new ArrayList<>();
        // give data to the adapter and create the adapter
        // passing context to get access to resources files
        mAdapter = new FriendRecyclerViewAdapter(this.getContext(), friends);
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
                long count = dataSnapshot.getChildrenCount();
                Log.d(TAG, "Children count: " + count);

                // Since the data has changed, we must delete the items
                friends.clear();
                Map<String, Integer> friendMap = (Map) dataSnapshot.child(user.getUid())
                        .child("friends").getValue();
                // if the user doesn't have a friend section or any friends
                if (friendMap == null){
                    // if no friends display the no friends message
                    recyclerView.setVisibility(View.INVISIBLE);
                    noFriendsDisplay.setVisibility(View.VISIBLE);
                    return;
                }

                Object[] friendUIDS = friendMap.keySet().toArray();
                for (Object i: friendUIDS){
                    String friendUID = (String) i;
                    String friendName = dataSnapshot.child(friendUID)
                            .child("name").getValue(String.class);
                    int friendAvatar = dataSnapshot.child(friendUID)
                            .child("avatar").getValue(Integer.class);
                    friends.add(new FriendEntry(friendName, friendUID, friendAvatar));
                }

                // sorting the friends for the display
                // uses that friends implements Comparable
                Collections.sort(friends);

                Log.d(TAG, "Importing Friends...");
                Log.d(TAG, "Imported: " + friends.size() + " friends");
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        // if they have no friends
        if (mAdapter.getItemCount() == 0) {
            Log.d(TAG, "Displaying No Friends Message");
            //noFriendsDisplay.setText(R.string.emptyFriends);
            //recyclerView.setVisibility(View.INVISIBLE);
            //noFriendsDisplay.setVisibility(View.VISIBLE);
        }


    }


    // declaring the RecyclerViewAdapter
    public static class FriendRecyclerViewAdapter
            extends RecyclerView.Adapter<FriendRecyclerViewAdapter.ViewHolder> {

        private ArrayList<FriendEntry> friends;
        private Context context;

        // implement to navigate to page when data is clicked
        //private View.OnClickListener friendEntryOnClickListener;

        // Provide a suitable constructor (depends on the kind of dataset)
        public FriendRecyclerViewAdapter(Context context, ArrayList<FriendEntry> friends) {
            this.friends = friends;
            this.context = context;
            Log.d(TAG, "Number of friends: " + friends.size());

        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // place to store data from database
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_entry, parent, false);

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            Log.d("binder", friends.get(position).toString());
            FriendEntry friend = friends.get(position);

            // setting friendName Data
            holder.nameTextView.setText(friend.getName());
            // setting avatar image
            TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);
            holder.avatarImageView.setImageDrawable(avatars.getDrawable(friend.getAvatar()));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {return friends.size();}

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView nameTextView;
            public ImageView avatarImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                nameTextView = (TextView) itemView.findViewById(R.id.friend_name);
                avatarImageView = (ImageView) itemView.findViewById(R.id.friend_avatar);
            }
        }
    }

}
