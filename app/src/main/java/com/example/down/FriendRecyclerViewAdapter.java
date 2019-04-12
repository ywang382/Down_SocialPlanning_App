package com.example.down;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

/**
 * This is the Adapter for the RecyclerView.
 */
public class FriendRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FriendEntry> friends;
    private ArrayList<FriendEntry> requests;
    private Context context;
    private final ClickListener listener;

    // implement to navigate to page when data is clicked
    //private View.OnClickListener friendEntryOnClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendRecyclerViewAdapter(Context context, ArrayList<FriendEntry> friends,
                                     ArrayList<FriendEntry> requests, ClickListener listener) {
        this.friends = friends;
        this.requests = requests;
        this.context = context;
        this.listener = listener;
        Log.d(TAG, "Number of friends: " + friends.size());
        Log.d(TAG, "Number of requests: " + requests.size());
    }

    // given what position we are in (when displaying the friendEntries)
    // which view should we return (0 = friend, 1 = request)
    @Override
    public int getItemViewType(int position) {
        if (position < this.requests.size()){
            return 1;
        } else {
            return 0;
        }
    }


    // Create new views (invoked by the layout manager)
    // int viewType determines whether it is a request or a friend
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // place to store data from database
        if (viewType == 0) { // it is a friend
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friend_entry, parent, false);
            return new FriendViewHolder(v);
        } else { // it is a request
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.request_entry, parent, false);
            return new RequestViewHolder(v, listener);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        FriendEntry person;
        Log.d(TAG, "Position " + position);
        Log.d(TAG, "Friend Size " + friends.size());
        Log.d(TAG, "Requests Size " + requests.size());
        if (position < requests.size()){
            person = requests.get(position);
        } else {
            person = friends.get(position - requests.size());
        }

        // getting the list of avatars
        TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);

        // if its a friend
        if (holder.getItemViewType() == 0) {
            FriendViewHolder specificHolder = (FriendViewHolder) holder;
            // setting view data
            specificHolder.nameTextView.setText(person.getName());
            specificHolder.avatarImageView.setImageDrawable(avatars.getDrawable(person.getAvatar()));
        } else { // if its a request
            RequestViewHolder specificHolder = (RequestViewHolder) holder;
            // setting view data
            specificHolder.nameTextView.setText(person.getName() + " - New Request");
            specificHolder.nameTextView.setTextColor(context.getResources().getColor(R.color.blue));
            specificHolder.avatarImageView.setImageDrawable(avatars.getDrawable(person.getAvatar()));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {return friends.size() + requests.size();}


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView nameTextView;
        private ImageView avatarImageView;

        public FriendViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.friend_name);
            avatarImageView = (ImageView) itemView.findViewById(R.id.request_avatar);
        }

    }
    public class RequestViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        // each data item is just a string in this case
        private TextView nameTextView;
        private ImageView avatarImageView;
        private Button accept;
        private Button reject;
        private WeakReference<ClickListener> listenerRef;

        public RequestViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.request_name);
            avatarImageView = (ImageView) itemView.findViewById(R.id.request_avatar);
            listenerRef = new WeakReference<>(listener);
            accept = (Button) itemView.findViewById(R.id.accept_request);
            reject = (Button) itemView.findViewById(R.id.reject_request);

            /* What to do when clicked. Note, this class implements onClickListener
            so this is actually a valid onClickListener. However, I pass the same listener
            to each object, so in the listener function below I conditionally choose which
            view I am using. CLD
             */
            accept.setOnClickListener(this);
            reject.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            boolean accepted;
            if (view.getId() == accept.getId()) {
                accepted = true;
                //db.child("name").updateChildren();
                Toast.makeText(view.getContext(), "Accepted", Toast.LENGTH_SHORT).show();
            } else {
                accepted = false;
                Toast.makeText(view.getContext(), "Rejected", Toast.LENGTH_SHORT).show();
            }
            listenerRef.get().onPositionClicked(getAdapterPosition(), accepted);
        }
    }
}
