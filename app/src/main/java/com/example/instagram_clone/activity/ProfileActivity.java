package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.utils.FollowHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private User selectedUser;
    private User loggedUser;
    private boolean isFollowed = false;

    private TextView numberPosts, numberFollowers, numberFollowing;
    private Button profileActionButton;
    private CircleImageView profileImage;
    private GridView gridView;

    private DatabaseReference selectedUserRef;
    private ValueEventListener valueEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Config Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change icon of back button

        loggedUser = UserHelper.getLogged();

        initViewElements();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverSelectedUserInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        selectedUserRef.removeEventListener(valueEventListener);
    }

    private void initViewElements() {
        profileImage = findViewById(R.id.circleImageViewProfile);
        profileActionButton = findViewById(R.id.profileActionButton);
        gridView = findViewById(R.id.gridView);
        numberPosts = findViewById(R.id.numberPosts);
        numberFollowers = findViewById(R.id.numberFollowers);
        numberFollowing = findViewById(R.id.numberFollowing);
    }

    /**
     * recover selected user info to build his profile page
     */
    private void recoverSelectedUserInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constants.IntentKey.SELECTED_USER)) {
            selectedUser = (User) bundle.getSerializable(Constants.IntentKey.SELECTED_USER);
            // Using listener only to user numbers, so other info will be showed faster
            setUserNumbersListener();

            configInterface();

            if (selectedUser.getPicturePath() != null) {
                Uri uri = Uri.parse(selectedUser.getPicturePath());
                Picasso.get().load(uri)
                        .placeholder(R.drawable.profile)
                        .into(profileImage);
            }
        }
    }

    /**
     * Listener to update posts, followers and following info about user,
     * So it can be updated instantaneously when user makes an action
     */
    private void setUserNumbersListener() {
        selectedUserRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(selectedUser.getId());
        valueEventListener =
                selectedUserRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        numberPosts.setText(String.valueOf(user.getCountPosts()));
                        numberFollowers.setText(String.valueOf(user.getCountFollowers()));
                        numberFollowing.setText(String.valueOf(user.getCountFollowing()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Apply differences from friend profile and user own profile,
     * like "follow button" being "edit profile" not allowing him to follow himself
     */
    private void configInterface() {
        if (selectedUser.getId().equals(loggedUser.getId()))
            configInterfaceToLoggedUser();
        else
            confiInterfaceToFriendProfile();
    }
    private void configInterfaceToLoggedUser() {
        getSupportActionBar().setTitle("Perfil");
        profileActionButton.setText("Editar Perfil");
        profileActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        });
    }
    private void confiInterfaceToFriendProfile() {
        getSupportActionBar().setTitle(selectedUser.getName());

        checkIfLoggedFollowSelectedUser();
        profileActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if is following, we unfollow when user clicks the button
                if (isFollowed) {
                    if (FollowHelper.unfollow(selectedUser))
                        updateNumbers(false);
                } else {
                    if (FollowHelper.follow(selectedUser))
                        updateNumbers(true);
                }
                checkIfLoggedFollowSelectedUser();
            }
        });
    }

    /**
     * Update numbers following and followers on loggedUser and selectedUser when
     * loggedUser follow or unfollow selectedUser
     */
    private void updateNumbers(boolean isFollowAction) {
        int countLoggedFollowing = loggedUser.getCountFollowing();
        int countSelectedUserFollowers = selectedUser.getCountFollowers();

        if (isFollowAction) {
            countLoggedFollowing++;
            countSelectedUserFollowers++;
        } else { // if its not a follow action, it's a unfollow action
            countLoggedFollowing--;
            countSelectedUserFollowers--;
        }

        loggedUser.setCountFollowing(countLoggedFollowing);
        selectedUser.setCountFollowers(countSelectedUserFollowers);

        UserHelper.updateOnDatabase(loggedUser);
        UserHelper.updateOnDatabase(selectedUser);
    }
    /**
     * check if selected user is followed or not by logged user
     */
    private void checkIfLoggedFollowSelectedUser() {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.FollowNode.KEY)
                .child(loggedUser.getId())
                .child(selectedUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            isFollowed = true;
                            profileActionButton.setBackground(getResources().getDrawable(R.drawable.back_profile_button_following));
                            profileActionButton.setTextColor(getResources().getColor(android.R.color.white));
                            profileActionButton.setText("Seguindo");
                        } else {
                            isFollowed = false;
                            profileActionButton.setBackground(getResources().getDrawable(R.drawable.back_profile_button));
                            profileActionButton.setTextColor(getResources().getColor(android.R.color.black));
                            profileActionButton.setText("Seguir");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    /**
     * Change behavior of app backbutton to work like native smartphone backbutton
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}