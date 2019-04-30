package com.example.down;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InGroupAdapterNewGroups extends RecyclerView.Adapter<InGroupAdapterNewGroups.SearchViewHolder> {
    Context context;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    ArrayList<Integer> avatarList;
    ArrayList<String> UIDList;
    ArrayList<GroupElement> addList = new ArrayList<GroupElement>();
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

    public InGroupAdapterNewGroups(Context context, ArrayList<String> nameList, ArrayList<String> emailList, ArrayList<Integer> avatarList, ArrayList<String> UIDList) {
        this.context = context;
        this.nameList = nameList;
        this.emailList = emailList;
        this.avatarList = avatarList;
        this.UIDList = UIDList;
    }

    @Override
    public InGroupAdapterNewGroups.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.groups_list_items, parent, false);
        return new InGroupAdapterNewGroups.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        //holder.name.setText(nameList.get(position));
        UID = (UIDList.get(position));

        TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);
        avatarIndex = (avatarList.get(position));
        Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);

        String arr[] = nameList.get(position).split(" ", 2);
        holder.name.setText(arr[0]);
        final GroupElement thisUser = new GroupElement(arr[0], UID, avatarIndex);
        setColor(UID, holder);

        //Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(R.drawable.avatar0).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(avatarList.get(position)).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);

        holder.entireView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustGroup(UID);
                setColor(UID, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public void adjustGroup(String UID) {
        if (CreateGroupActivity.selUIDList != null && CreateGroupActivity.selUIDList.contains(UID)) {
            CreateGroupActivity.selUIDList.remove(UID);
        } else {
            CreateGroupActivity.selUIDList.add(UID);
        }
    }

    public class GroupElement
    {
        // Instance Variables
        String name;
        String UID;
        int avatarIndex;

        // Constructor Declaration of Class
        public GroupElement(String name, String UID,
                   int avatarIndex)
        {
            this.name = name;
            this.UID = UID;
            this.avatarIndex = avatarIndex;
        }

        // method 1
        public String getName()
        {
            return name;
        }

        // method 2
        public String getUID()
        {
            return UID;
        }

        // method 3
        public int getAvatarIndex()
        {
            return avatarIndex;
        }
    }

    /*
    public void ifSel(final SearchViewHolder holder, int position) {
        if (selectList.get(position)) {
            selectList.set(position, false);
            holder.entireView.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            selectList.set(position, true);
            holder.entireView.setBackgroundColor(Color.parseColor("#909aa0"));
        }

    }
    */

    public void setColor(String UID, final SearchViewHolder holder) {
            if (CreateGroupActivity.selUIDList != null && CreateGroupActivity.selUIDList.contains(UID)) {
                holder.entireView.setBackgroundColor(Color.parseColor("#909aa0"));
            } else {
                holder.entireView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
    }

    public void setColor(GroupElement thisUser, final SearchViewHolder holder, Boolean sel) {
        if (addList.contains(thisUser)) {
            holder.entireView.setBackgroundColor(Color.parseColor("#909aa0"));

        } else {
            holder.entireView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

}