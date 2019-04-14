package com.example.down;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
public class ChooseFriendRecyclerViewAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FriendEntry> friends;
    private ArrayList<FriendEntry> groups;
    private Context context;

    // implement to navigate to page when data is clicked
    //private View.OnClickListener friendEntryOnClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChooseFriendRecyclerViewAdapter(Context context, ArrayList<FriendEntry> friends,
                                     ArrayList<FriendEntry> groups) {
        this.friends = friends;
        //this.requests = requests;
        this.context = context;
        Log.d(TAG, "Number of friends: " + friends.size());
        //Log.d(TAG, "Number of groups: " + groups.size());
    }

    // given what position we are in (when displaying the friendEntries)
    // which view should we return (0 = friend, 1 = request)
    @Override
    public int getItemViewType(int position) {
        /*if (position < this.groups.size()){
            return 1;
        } else {
            return 0;
        }*/
        // always returning 0 until we figure out groups
        return 0;
    }


    // Create new views (invoked by the layout manager)
    // int viewType determines whether it is a request or a friend
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // place to store data from database
        //if (viewType == 0) { // it is a friend
        // things are commented out because we will only be dealing with friends
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_friend_entry, parent, false);
        return new ChooseFriendViewHolder(v);
        //}
        /*else { // it is a request
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.request_entry, parent, false);
            return new RequestViewHolder(v, listener);
        }*/
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Log.d(TAG, "Position " + position);
        final FriendEntry person = this.friends.get(position);
        final TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);
        final Drawable checkbox_on = context.getResources().getDrawable(R.drawable.checkbox_on_background);
        final Drawable checkbox_off = context.getResources().getDrawable(R.drawable.checkbox_off_background);

        final ChooseFriendViewHolder specificHolder = (ChooseFriendViewHolder) holder;
        // setting view data
        specificHolder.nameTextView.setText(person.getName());
        specificHolder.checkbox.setImageDrawable(checkbox_off);
        specificHolder.entireView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               person.setSelected(!person.getSelect());
               if (person.getSelect()) {
                   specificHolder.checkbox.setImageDrawable(checkbox_on);
               } else {
                   specificHolder.checkbox.setImageDrawable(checkbox_off);
               }
           }
        });

        //Log.d(TAG, "Friend Size " + friends.size());
        //Log.d(TAG, "Requests Size " + groups.size());
        /*if (position < requests.size()){
            person = requests.get(position);
        } else {
            person = friends.get(position - requests.size());
        }*/


        // if its a friend
        /*if (holder.getItemViewType() == 0) {
            ChooseFriendViewHolder specificHolder = (ChooseFriendViewHolder) holder;
            // setting view data
            specificHolder.nameTextView.setText(person.getName());

        }
        else { // if its groups
            RequestViewHolder specificHolder = (RequestViewHolder) holder;
            // setting view data
            specificHolder.nameTextView.setText(person.getName() + context.getResources()
                    .getString(R.string.newRequest));
            specificHolder.nameTextView.setTextColor(context.getResources().getColor(R.color.blue));
            specificHolder.avatarImageView.setImageDrawable(avatars.getDrawable(person.getAvatar()));
        }*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {return friends.size();}


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ChooseFriendViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView nameTextView;
        private ImageView checkbox;
        private View entireView;

        public ChooseFriendViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.choose_friend_name);
            checkbox = (ImageView) itemView.findViewById(R.id.checkboxIcon);
            entireView = (View) itemView;
        }

    }
}

