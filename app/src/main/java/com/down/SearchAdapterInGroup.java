package com.down;

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
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }
}