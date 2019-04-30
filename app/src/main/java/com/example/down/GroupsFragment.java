package com.example.down;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class GroupsFragment extends Fragment {
    EditText search_edit_text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    SearchAdapterYourGroups searchAdapterYourGroups;

    ArrayList<String> groupNameList;
    ArrayList<ArrayList<String>> groupUIDList;
    ArrayList<String> groupDescriptList;
    DatabaseReference userGroups;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setting the title to the new page
        getActivity().setTitle(R.string.title_fragment_my_groups);

        /*
        Toolbar mToolbar = (Toolbar) this.getView().findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_activity_add_friend));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        */

        FloatingActionButton fab = this.getView().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                startActivity(intent);
            }
        });
        search_edit_text = (EditText) this.getView().findViewById(R.id.search_edit_text);
        recyclerView = (RecyclerView) this.getView().findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();


        userGroups = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));

        //Creates a array list for each node

        groupNameList = new ArrayList<>();
        groupDescriptList = new ArrayList<>();
        groupUIDList = new ArrayList<>();


        //Sets adapter to display friends options
        setAdapter("");

        //Method to change what friends are displayed with change in text
        search_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());
                } else {
                    /*
                     * Clear the list when editText is empty
                     * */

                    groupNameList.clear();
                    groupDescriptList.clear();
                    groupUIDList.clear();

                    recyclerView.removeAllViews();
                    setAdapter("");
                }
            }
        });
    }
    private void setAdapter(final String searchedString) {


        userGroups.child("groups").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                recyclerView.removeAllViews();

                groupNameList.clear();
                groupDescriptList.clear();
                groupUIDList.clear();

                int counter = 0;

                if (searchedString.length() == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String groupName = snapshot.getKey();
                        ArrayList<String> membersList = new ArrayList<>();
                        ArrayList<String> nameList = new ArrayList<>();
                        String groupDescript = "";
                        int i = 0;
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            i++;
                            membersList.add(snap.getKey());
                            if (i <= 3) {
                                String name = snap.getValue().toString();
                                nameList.add(name);
                            }
                        }

                        switch (i) {
                            case 1:
                                groupDescript = nameList.get(0);
                                break;
                            case 2:
                                groupDescript = nameList.get(0) + " and " + nameList.get(1);
                                break;
                            case 3:
                                groupDescript = nameList.get(0) + ", " + nameList.get(1) + " and " + nameList.get(2);
                                break;
                            default:
                                groupDescript = nameList.get(0) + ", " + nameList.get(1) + ", " + nameList.get(2) + " and " + (i - 3) + " others";
                        }

                        groupUIDList.add(membersList);
                        groupNameList.add(groupName);
                        groupDescriptList.add(groupDescript);

                        counter++;

                        if (counter == 15)
                            break;
                    }

                } else {

                    /*
                     * Search all users for matching searched string
                     * */
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("friends").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            continue;
                        }

                        String groupName = snapshot.getKey();
                        ArrayList<String> membersList = new ArrayList<>();
                        ArrayList<String> nameList = new ArrayList<>();
                        String groupDescript = "";
                        int i = 0;
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            i++;
                            membersList.add(snap.getKey());
                            if (i <= 3) {
                                String name = snap.getValue().toString();
                                nameList.add(name);
                            }
                        }

                        switch (i) {
                            case 1:
                                groupDescript = nameList.get(0);
                                break;
                            case 2:
                                groupDescript = nameList.get(0) + " and " + nameList.get(1);
                                break;
                            case 3:
                                groupDescript = nameList.get(0) + ", " + nameList.get(1) + " and " + nameList.get(2);
                                break;
                            default:
                                groupDescript = nameList.get(0) + ", " + nameList.get(1) + ", " + nameList.get(2) + " and " + (i - 3) + " others";
                        }

                        if (groupName.toLowerCase().contains(searchedString.toLowerCase())) {

                            groupUIDList.add(membersList);
                            groupNameList.add(groupName);
                            groupDescriptList.add(groupDescript);
                            counter++;
                        }

                        else if (groupDescript.toLowerCase().contains(searchedString.toLowerCase())) {
                            groupUIDList.add(membersList);
                            groupNameList.add(groupName);
                            groupDescriptList.add(groupDescript);
                            counter++;
                        }


                        /*
                         * Get maximum of 15 searched results only
                         * */
                        if (counter == 15)
                            break;
                    }
                }

                searchAdapterYourGroups = new SearchAdapterYourGroups(getContext(), groupNameList, groupDescriptList, groupUIDList);

                //SearchAdapter(AddFriendActivity.this, nameList, emailList);
                recyclerView.setAdapter(searchAdapterYourGroups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
