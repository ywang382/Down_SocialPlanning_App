package com.example.down;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
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

public class SearchAdapterInGroup extends RecyclerView.Adapter<SearchAdapterInGroup.SearchViewHolder> {
    Context context;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    ArrayList<Integer> avatarList;
    ArrayList<String> UIDList;
    String UIDi;
    String userUID;
    Integer avatarIndex;
    private String groupName;


    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ImageView avatarImage;
        View entireView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatarImage);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            entireView = itemView;
        }
    }

    public SearchAdapterInGroup(Context context, ArrayList<String> nameList, ArrayList<String> emailList,
                                ArrayList<Integer> avatarList, ArrayList<String> UIDList, String UID, String gName) {
        this.context = context;
        this.nameList = nameList;
        this.emailList = emailList;
        this.avatarList = avatarList;
        this.UIDList = UIDList;
        this.userUID = UID;
        this.groupName = gName;
    }

    @Override
    public SearchAdapterInGroup.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false);
        return new SearchAdapterInGroup.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, final int position) {
        holder.name.setText(nameList.get(position));
        holder.email.setText(emailList.get(position));
        UIDi = (UIDList.get(position));


        TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);
        avatarIndex = (avatarList.get(position));
        Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);


        //Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(R.drawable.avatar0).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(avatarList.get(position)).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);

        holder.entireView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                buildHer(UIDList.get(position), nameList.get(position));
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public void buildHer (final String uID, final String userName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Removing Friend From Group");
        builder.setMessage("By clicking yes, you will remove " + userName + " from this group");
        builder.setCancelable(false);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "You have removed " + userName + " from the group", Toast.LENGTH_SHORT).show();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                db.child(userID).child("groups").child(groupName).child(uID).removeValue();
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