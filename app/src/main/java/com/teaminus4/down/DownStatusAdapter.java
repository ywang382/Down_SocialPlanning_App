package com.teaminus4.down;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DownStatusAdapter extends
        RecyclerView.Adapter<DownStatusAdapter.ViewHolder> {

    private Context myContext;
    private ArrayList<String> invited;
    private String myDown;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row

        public TextView userName;
        public TextView status;
        public ImageView avatar;
        public ImageView downButton;
        public TypedArray avatars;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            status = (TextView) itemView.findViewById(R.id.user_status);
            avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            downButton = (ImageView) itemView.findViewById(R.id.down_button);
            avatars = myContext.getResources().obtainTypedArray(R.array.avatar_imgs);
        }
    }

    public DownStatusAdapter(Context context, ArrayList<String> users, String downID){
        myContext = context;
        invited = users;
        myDown = downID;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public DownStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.recycler_down_status, parent, false);

        // Return a new holder instance
        return new ViewHolder(v);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final DownStatusAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final String curUser = invited.get(position);
        final DownStatusAdapter.ViewHolder holder = viewHolder;
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                DataSnapshot ds1 = ds.child("down").child(myDown);
                DataSnapshot ds2 = ds.child("users").child(curUser);
                viewHolder.avatar.setImageDrawable(viewHolder.avatars.getDrawable(ds2.child("avatar").getValue(Integer.class)));
                viewHolder.userName.setText(ds2.child("name").getValue(String.class));
                if(!ds1.hasChildren()){ return;}
                String st = ds1.child("status").child(curUser).getValue(String.class);
                viewHolder.status.setText((st.isEmpty()) ? "..." : st);

                if(ds1.child("invited").child(curUser).getValue(Integer.class) == 1){
                    viewHolder.downButton.setImageResource(R.drawable.down);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError ds) {

            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return invited.size();
    }
}
