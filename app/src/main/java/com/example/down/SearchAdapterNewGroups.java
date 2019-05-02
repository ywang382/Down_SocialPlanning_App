package com.example.down;

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

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.down.CreateGroupActivity.avatarAddList;
import static com.example.down.CreateGroupActivity.emailAddList;
import static com.example.down.CreateGroupActivity.nameAddList;
import static com.example.down.CreateGroupActivity.recyclerView2;
import static com.example.down.CreateGroupActivity.selUIDList;

public class SearchAdapterNewGroups extends RecyclerView.Adapter<SearchAdapterNewGroups.SearchViewHolder> {
    Context context;
    private ArrayList<String> nameList;
    private ArrayList<String> emailList;
    private ArrayList<Integer> avatarList;
    private ArrayList<String> UIDList;
    ArrayList<String> selectList = new ArrayList<String>();
    ArrayList<String> addListUID = new ArrayList<String>();
    ArrayList<String> addListName = new ArrayList<String>();
    ArrayList<Integer> addListAvatar = new ArrayList<Integer>();
    ArrayList<GroupElement> addList = new ArrayList<GroupElement>();
    String UID;
    Boolean sel;
    Integer avatarIndex;
    InGroupAdapterNewGroups inGroupAdapterNewGroups;



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

        final TypedArray avatars = this.context.getResources().obtainTypedArray(R.array.avatar_imgs);
        avatarIndex = (avatarList.get(position));
        Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);

        String arr[] = nameList.get(position).split(" ", 2);
        final GroupElement thisUser = new GroupElement(arr[0], UID, avatarIndex);
        setColor(UID, holder);

        inGroupAdapterNewGroups = new InGroupAdapterNewGroups(context, nameAddList, emailAddList, avatarAddList, selUIDList);
        recyclerView2.setAdapter(inGroupAdapterNewGroups);
        setColor(UIDList.get(position), holder);

        //Glide.with(context).load(avatars.getDrawable(avatarIndex)).placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(R.drawable.avatar0).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);
        //Glide.with(context).load(avatarList.get(position)).asBitmap().placeholder(R.mipmap.ic_launcher_round).into(holder.avatarImage);

        holder.entireView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //adjustGroup(nameList.get(position), emailList.get(position), avatarList.get(position), UID);
                recyclerView2.removeAllViews();
                int index = selUIDList.indexOf(UIDList.get(position));
                if(index != -1){
                    selUIDList.remove(index);
                    nameAddList.remove(index);
                    emailAddList.remove(index);
                    avatarAddList.remove(index);
                } else {
                    nameAddList.add(nameList.get(position));
                    emailAddList.add(emailList.get(position));
                    avatarAddList.add(avatarList.get(position));
                    selUIDList.add(UIDList.get(position));
                }

                inGroupAdapterNewGroups = new InGroupAdapterNewGroups(context, nameAddList, emailAddList, avatarAddList, selUIDList);
                recyclerView2.setAdapter(inGroupAdapterNewGroups);
                setColor(UIDList.get(position), holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
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
        if (selUIDList.contains(UID)) {
            holder.entireView.setBackgroundColor(Color.parseColor("#B8F0D1"));
        } else {
            holder.entireView.setBackgroundColor(Color.parseColor("#ffffff"));

        }
    }


}