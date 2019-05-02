package com.example.down;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;

public class FriendProfileActivity extends AppCompatActivity {

    private ImageView backButton;
    private ImageView removeButton;
    //private ImageView topGreen;
    private ImageView avatarImage;
    private TextView name_tv;
    private TextView email_tv;
    private TextView friendsCount_tv;
    private TextView downCount_tv;
    private TextView scoreHead_tv;
    private TextView scoreOne_tv;
    private TextView scoreTwo_tv;
    private TextView scoreThree_tv;
    public String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_user_profile);

        backButton = (ImageView) findViewById(R.id.back_button);
        removeButton = (ImageView) findViewById(R.id.signout_button);
        removeButton.setImageResource(R.drawable.ic_delete_friend);
        avatarImage = (ImageView) findViewById(R.id.friend_avatar_image);
        name_tv = (TextView) findViewById(R.id.name_tv);

        email_tv = (TextView) findViewById(R.id.email_tv);
        friendsCount_tv = (TextView) findViewById(R.id.friends_count_tv);
        downCount_tv = (TextView) findViewById(R.id.downs_count_tv);
        scoreHead_tv = (TextView) findViewById(R.id.account_setting);
        scoreOne_tv = (TextView) findViewById(R.id.score1_tv);
        scoreTwo_tv = (TextView) findViewById(R.id.score2_tv);
        scoreThree_tv = (TextView) findViewById(R.id.score3_tv);
        getSupportActionBar().hide();

        Bundle bundle = getIntent().getExtras();

        uid = bundle.getString("userID");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(uid);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                buildAlert();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                scoreHead_tv.setVisibility(View.INVISIBLE);
                scoreOne_tv.setVisibility(View.INVISIBLE);
                scoreTwo_tv.setVisibility(View.INVISIBLE);
                scoreThree_tv.setVisibility(View.INVISIBLE);

                int avatar = dataSnapshot.child("avatar").getValue(Integer.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                long numFriends = dataSnapshot.child("friends").getChildrenCount();
                long numDowns = dataSnapshot.child("downs").getChildrenCount();

                /*Map<String, Integer> friendMap = (Map) dataSnapshot.child(uid)
                        .child("friends").getValue();

                ArrayList<FriendEntry> friends = new ArrayList<>();*/

                friendsCount_tv.setText(numFriends + " friends");
                downCount_tv.setText(numDowns + " downs");

                TypedArray avatars = getResources().obtainTypedArray(R.array.avatar_imgs);
                avatarImage.setImageDrawable(avatars.getDrawable(avatar));
                name_tv.setText(name);
                email_tv.setText(email);

                Log.d(TAG, "Avatar: " + avatar);
                Log.d(TAG, "Email: " + email);
                Log.d(TAG, "Name: " + name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void buildAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_friend_title);
        builder.setMessage(getString(R.string.delete_friend_dialog));
        builder.setCancelable(false);

        builder.setPositiveButton("Remove Friend", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
                final String curUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                db.child(curUser).child("friends").child(uid).removeValue();
                db.child(uid).child("friends").child(curUser).removeValue();
                // Remove each other in groups.
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        for(DataSnapshot group : ds.child(uid).child("groups").getChildren()){
                            for(DataSnapshot member : group.getChildren()){
                                if(member.getKey().equals(curUser))
                                    member.getRef().removeValue();
                            }
                        }
                        for(DataSnapshot group : ds.child(curUser).child("groups").getChildren()){
                            for(DataSnapshot member : group.getChildren()){
                                if(member.getKey().equals(uid))
                                    member.getRef().removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
    }
}
