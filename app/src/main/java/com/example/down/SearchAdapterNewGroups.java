package com.example.down;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import java.util.Arrays;

public class SearchAdapterNewGroups extends RecyclerView.Adapter<SearchAdapterNewGroups.SearchViewHolder> {
    Context context;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    ArrayList<Integer> avatarList;
    ArrayList<String> UIDList;
    ArrayList<Boolean> selectList;
    ArrayList<String> addListUID = new ArrayList<String>();
    ArrayList<String> addListName = new ArrayList<String>();
    ArrayList<Integer> addListAvatar = new ArrayList<Integer>();
    String UID;
    Integer avatarIndex;



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

    public SearchAdapterNewGroups(Context context, ArrayList<String> nameList, ArrayList<String> emailList, ArrayList<Integer> avatarList, ArrayList<String> UIDList) {
        this.context = context;
        this.nameList = nameList;
        this.emailList = emailList;
        this.avatarList = avatarList;
        this.UIDList = UIDList;

        this.selectList = new ArrayList<Boolean>(getItemCount());
        //Arrays.fill(selectList, false);
        for (int i = 0; i < getItemCount(); i++) {
            selectList.add(false);
        }
    }

    @Override
    public SearchAdapterNewGroups.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_items, parent, false);
        return new SearchAdapterNewGroups.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        holder.name.setText(nameList.get(position));
        holder.email.setText(emailList.get(position));
        UID = (UIDList.get(position));
        selectList.set(position, false);


        TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);
        avatarIndex = (avatarList.get(position));
        Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);


        //Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(R.drawable.avatar0).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(avatarList.get(position)).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);

        holder.entireView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToGroup(UIDList.get(position), nameList.get(position), avatarIndex);
                ifSel(holder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public void addToGroup(final String uID, final String userName, final Integer index) {
        String i = uID;
        addListUID.add(uID);
        String arr[] = userName.split(" ", 2);
        addListName.add(arr[0]);
        addListAvatar.add(index);
    }

    public void ifSel(final SearchViewHolder holder, int position) {
        if (selectList.get(position)) {
            selectList.set(position, false);
            holder.entireView.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            selectList.set(position, true);
            holder.entireView.setBackgroundColor(Color.parseColor("#909aa0"));
        }

    }
}