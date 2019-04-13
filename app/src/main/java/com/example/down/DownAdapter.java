package com.example.down;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.ArrayList;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DownAdapter extends
        RecyclerView.Adapter<DownAdapter.ViewHolder> {

    private Context myContext;
    private ArrayList<DownEntry> myDowns;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public TextView titleText;
        public TextView timeText;
        public TextView invitedText;
        public TextView downText;
        public ImageView downButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.title);
            timeText = (TextView) itemView.findViewById(R.id.time_and_person);
            invitedText = (TextView) itemView.findViewById(R.id.invited);
            downText = (TextView) itemView.findViewById(R.id.people_down);
            downButton = (ImageView) itemView.findViewById(R.id.down_button);
        }
    }

    public DownAdapter(Context context, ArrayList<DownEntry> downs){
        myContext = context;
        myDowns = downs;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public DownAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.recycler_downs, parent, false);

        // Return a new holder instance
        return new ViewHolder(v);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final DownAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final DownEntry d = myDowns.get(position);
        final DownAdapter.ViewHolder holder = viewHolder;

        viewHolder.titleText.setText(d.title);
        viewHolder.invitedText.setText(d.nInvited + " people invited");
        viewHolder.timeText.setText(d.time + " - " + d.creator);
        viewHolder.downText.setText(d.nDown + " people are down!");
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        if(d.isDown){
            viewHolder.downButton.setImageResource(R.drawable.down);
        }
        viewHolder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.downButton.getDrawable().getConstantState()
                        == myContext.getResources().getDrawable(R.drawable.notdown).getConstantState()){
                    holder.downButton.setImageResource(R.drawable.down);
                    db.child("users").child(uid).child("downs").child(d.id).setValue(1);
                    DatabaseReference downRef = db.child("down").child(d.id);
                    downRef.child("invited").child(uid).setValue(1);
                    downRef.child("nDown").setValue(d.nDown + 1);
                    Toast.makeText(myContext, "You are down to " + d.title, Toast.LENGTH_SHORT).show();
                } else{
                    holder.downButton.setImageResource(R.drawable.notdown);
                    db.child("users").child(uid).child("downs").child(d.id).setValue(0);
                    DatabaseReference downRef = db.child("down").child(d.id);
                    downRef.child("invited").child(uid).setValue(0);
                    downRef.child("nDown").setValue(d.nDown - 1);
                    Toast.makeText(myContext, "You are no longer down to " + d.title, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return myDowns.size();
    }
}
