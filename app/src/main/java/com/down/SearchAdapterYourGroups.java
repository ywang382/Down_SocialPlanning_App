package com.down;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SearchAdapterYourGroups extends RecyclerView.Adapter<SearchAdapterYourGroups.SearchViewHolder> {
    Context context;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    ArrayList<Integer> avatarList;
    ArrayList<String> UIDList;

    ArrayList<String> groupNameList;
    ArrayList<String> groupDescriptList;
    ArrayList<ArrayList<String>> groupUIDList;

    String UID;
    Integer avatarIndex;


    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        View entireView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            entireView = itemView;
        }
    }

    public SearchAdapterYourGroups(Context context, ArrayList<String> groupNameList, ArrayList<String> groupDescriptList, ArrayList<ArrayList<String>> groupUIDList) {
        this.context = context;
        this.groupNameList = groupNameList;
        this.groupDescriptList = groupDescriptList;
        this.groupUIDList = groupUIDList;
    }

    @Override
    public SearchAdapterYourGroups.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_groups_list_items, parent, false);
        return new SearchAdapterYourGroups.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        holder.name.setText(groupNameList.get(position));
        holder.email.setText(groupDescriptList.get(position));
        //UID = (UIDList.get(position));

        holder.entireView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupClickedActivity.class);
                Bundle extras = new Bundle();
                extras.putString("GROUP_NAME", groupNameList.get(position));
                extras.putStringArrayList("GROUP_UIDS", groupUIDList.get(position));
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });

        holder.entireView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                buildHer(groupNameList.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupNameList.size();
    }

    public void buildHer (final String groupName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deleting Group");
        builder.setMessage("By clicking remove, you will remove the group " + groupName + ". Doing so will not remove your friends belonging to the group.");

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "You have removed " + groupName, Toast.LENGTH_SHORT).show();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                db.child(userID).child("groups").child(groupName).removeValue();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.red));
    }
    
}