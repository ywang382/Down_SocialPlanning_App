package com.example.down;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyFeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FeedFragment()).commit();

        user = FirebaseAuth.getInstance().getCurrentUser();
        View header = navigationView.getHeaderView(0);
        final TextView name = (TextView) header.findViewById(R.id.user_name);
        final TextView email = (TextView) header.findViewById(R.id.user_email);
        email.setText(user.getEmail());
        final ImageView avatar = header.findViewById(R.id.imageView);


        db = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue(String.class));
                int avatarId = dataSnapshot.child("avatar").getValue(Integer.class);
                TypedArray avatars = getResources().obtainTypedArray(R.array.avatar_imgs);
                avatar.setImageDrawable(avatars.getDrawable(avatarId));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Debug", databaseError.getDetails());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_feed) {
            fragment = new FeedFragment();
        } else if (id == R.id.nav_schedule) {
            fragment = new ScheduleFragment();
        } else if (id == R.id.nav_friends) {
            fragment = new FriendsFragment();
        } else if (id == R.id.nav_groups) {
            fragment = new GroupsFragment();
        } else if (id == R.id.nav_settings) {
            // code was previously used for sign-in / sign-out
            /*FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();*/

        } else if (id == R.id.nav_account) {
            Intent i = new Intent(this, PersonalUserProfile.class);
            startActivity(i);
        }

        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
