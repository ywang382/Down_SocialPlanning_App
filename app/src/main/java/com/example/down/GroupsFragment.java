package com.example.down;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;


public class GroupsFragment extends Fragment {
    EditText search_edit_text;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<String> nameList;
    ArrayList<String> emailList;
    ArrayList<Integer> avatarList;
    ArrayList<String> UIDList;
    SearchAdapterYourGroups searchAdapterYourGroups;

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

        search_edit_text = (EditText) this.getView().findViewById(R.id.search_edit_text);
        recyclerView = (RecyclerView) this.getView().findViewById(R.id.recyclerView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));

        //Creates a array list for each node
        nameList = new ArrayList<>();
        emailList = new ArrayList<>();
        avatarList = new ArrayList<>();
        UIDList = new ArrayList<>();

        //Sets adapter to display friends options
        setAdapter(" ");

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
                    nameList.clear();
                    emailList.clear();
                    avatarList.clear();
                    UIDList.clear();
                    recyclerView.removeAllViews();
                    setAdapter(" ");
                }
            }
        });
    }
    private void setAdapter(final String searchedString) {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                nameList.clear();
                emailList.clear();
                avatarList.clear();
                UIDList.clear();
                recyclerView.removeAllViews();

                int counter = 0;

                /*
                 * Search all users for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.child("friends").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        continue;
                    }

                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String UID = snapshot.getKey();
                    Integer avatarIndex = snapshot.child("avatar").getValue(Integer.class);

                    if (name.toLowerCase().contains(searchedString.toLowerCase())) {
                        nameList.add(name);
                        emailList.add(email);
                        UIDList.add(UID);
                        avatarList.add(avatarIndex);
                        counter++;
                    } else if (email.toLowerCase().contains(searchedString.toLowerCase())) {
                        nameList.add(name);
                        emailList.add(email);
                        UIDList.add(UID);
                        avatarList.add(avatarIndex);
                        counter++;
                    }

                    /*
                     * Get maximum of 15 searched results only
                     * */
                    if (counter == 15)
                        break;
                }

                searchAdapterYourGroups = new SearchAdapterYourGroups(getContext(), nameList, emailList, avatarList, UIDList);

                //SearchAdapterYourGroups(AddFriendActivity.this, nameList, emailList);
                recyclerView.setAdapter(searchAdapterYourGroups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
