package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.instagram_clone.MainActivity;
import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.GridAdapter;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.utils.FollowHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.MessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static ProfileActivity profileActivity;
    private User selectedUser;
    private User loggedUser;
    private boolean isFollowed = false;

    private TextView labelNumberPosts, labelNumberFollowers, labelNumberFollowing;
    private Button profileActionButton;
    private CircleImageView profileImage;
    private BottomNavigationViewEx profileBottomNavigation;


    private GridView gridView;
    private GridAdapter gridAdapter;
    private List<Post> postsList = new ArrayList<>();

    private DatabaseReference selectedUserRef;
    private ValueEventListener selectedUserEventListener;
    private ValueEventListener loggedUserEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        profileActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Config Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewElements();
    }

    @Override
    protected void onStart() {
        super.onStart();

        loggedUserEventListener = UserHelper.getLoggedCompleteInfo()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loggedUser = dataSnapshot.getValue(User.class);
                            recoverBasicSelectedUserInfo();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (selectedUserEventListener != null)
            selectedUserRef.removeEventListener(selectedUserEventListener);

        if (loggedUserEventListener != null)
            UserHelper.getLoggedCompleteInfo().removeEventListener(loggedUserEventListener);
    }

    private void initViewElements() {
        profileImage = findViewById(R.id.circleImageViewProfile);
        profileActionButton = findViewById(R.id.profileActionButton);
        gridView = findViewById(R.id.gridView);
        labelNumberPosts = findViewById(R.id.numberPosts);
        labelNumberFollowers = findViewById(R.id.numberFollowers);
        labelNumberFollowing = findViewById(R.id.numberFollowing);
        profileBottomNavigation = findViewById(R.id.profileBottomNav);

        // animations
        profileBottomNavigation.enableAnimation(false);
        profileBottomNavigation.enableItemShiftingMode(false);
        profileBottomNavigation.enableShiftingMode(false);
        profileBottomNavigation.setTextVisibility(false);
    }

    /**
     * recover selected user info to build his profile page
     */
    private void recoverBasicSelectedUserInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constants.IntentKey.SELECTED_USER)) {
            selectedUser = (User) bundle.getSerializable(Constants.IntentKey.SELECTED_USER);
            // Using listener only to user numbers, so other info will be showed faster
            recoverCompleteSelectedUserInfo();
        }
    }

    /**
     * Listener to update posts, followers and following info about user,
     * So it can be updated instantaneously when user makes an action
     */
    private void recoverCompleteSelectedUserInfo() {
        selectedUserRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(selectedUser.getId());
        selectedUserEventListener =
                selectedUserRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            selectedUser = dataSnapshot.getValue(User.class);
                            // here we have both logged user and selectedUser info
                            // so we can build interface values
                            configInterface();
                        } else {
                            MessageHelper.showLongToast("Usuário inexistente");
                            finish();
                        }

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
        if (selectedUser.getCountPosts() != null) labelNumberPosts.setText(String.valueOf(selectedUser.getCountPosts()));
        if (selectedUser.getCountFollowers() != null) labelNumberFollowers.setText(String.valueOf(selectedUser.getCountFollowers()));
        if (selectedUser.getCountFollowing() != null) labelNumberFollowing.setText(String.valueOf(selectedUser.getCountFollowing()));

        if (selectedUser.getImagePath() != null) {
            Uri uri = Uri.parse(selectedUser.getImagePath());
            Picasso.get().load(uri)
                    .placeholder(R.drawable.profile)
                    .into(profileImage);
        }

        if (selectedUser.getId().equals(loggedUser.getId()))
            configInterfaceToLoggedUser();
        else
            confiInterfaceToFriendProfile();
        loadUserPosts();
        configPostClickListener();
    }
    private void configInterfaceToLoggedUser() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        profileActionButton.setText("Editar Perfil");
        profileActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
            }
        });
        configLongClickToDeletePostListener();
        configBottomNavigation();
    }
    private void confiInterfaceToFriendProfile() {

        profileBottomNavigation.setVisibility(View.GONE);

        getSupportActionBar().setTitle(selectedUser.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change icon of back button

        checkIfLoggedFollowSelectedUser();
        profileActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if is following, we unfollow when user clicks the button
                if (isFollowed) {
                    FollowHelper.unfollow(loggedUser, selectedUser);
                    isFollowed = false;
                }
                else {
                    FollowHelper.follow(loggedUser, selectedUser);
                    isFollowed = true;
                }
                checkIfLoggedFollowSelectedUser();
            }
        });
    }

    /**
     * Config grid adapter to show posts of user as grid
     */
    private void configGridView(List<Post> posts) {
        gridAdapter = new GridAdapter(getApplicationContext(), R.layout.post_list_item, posts);
        gridView.setAdapter(gridAdapter);
    }
    /**
     * Load user posts on gridView
     */
    private void loadUserPosts() {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PostNode.KEY)
                .child(selectedUser.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postsList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Post post = data.getValue(Post.class);
                                postsList.add(post);
                            }
                            Collections.reverse(postsList);
                            configGridView(postsList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }


    private void configPostClickListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostInfoActivity.class);
                intent.putExtra(Constants.IntentKey.SELECTED_USER, selectedUser);
                intent.putExtra(Constants.IntentKey.SELECTED_POST, postsList.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * you must implement this method only if user is on his profile
     */
    private void configLongClickToDeletePostListener() {
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);
                alert.setTitle("Tem certeza que deseja excluir o post?");
                alert.setMessage("Essa operação não pode ser desfeita");
                alert.setNegativeButton("Cancelar", null);
                alert.setPositiveButton("Sim, tenho certeza", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // double check user on his profile
                        if (selectedUser.getId().equals(loggedUser.getId())) {

                            Post postToRemove = postsList.get(position);
                            if (PostHelper.removeOnDatabase(postToRemove, loggedUser.getFollowersId())) {
                                loggedUser.decrementCountPosts();
                                if (UserHelper.updateOnDatabase(loggedUser)) {
                                    postsList.remove(postToRemove);
                                    configGridView(postsList);
                                    MessageHelper.showLongToast("Foto removida com sucesso");
                                }
                            };
                        }

                    }
                });
                alert.create();
                alert.show();
                return true;
            }
        });
    }

//    /**
//     * Update numbers following and followers on loggedUser and selectedUser when
//     * loggedUser follow or unfollow selectedUser
//     * @param isFollowed to check userIs followed
//     */
//    private void updateNumbers(boolean isFollowed) {
//        int countLoggedFollowing = loggedUser.getCountFollowing();
//        int countSelectedUserFollowers = selectedUser.getCountFollowers();
//
//        if (isFollowed) {
//            countLoggedFollowing++;
//            countSelectedUserFollowers++;
//        } else { // if its not a follow action, it's a unfollow action
//            countLoggedFollowing--;
//            countSelectedUserFollowers--;
//        }
//
//        loggedUser.setCountFollowing(countLoggedFollowing);
//        selectedUser.setCountFollowers(countSelectedUserFollowers);
//
//        UserHelper.updateOnDatabase(loggedUser);
//        UserHelper.updateOnDatabase(selectedUser);
//    }
    /**
     * check if logged user is one of the selectedUser followers to change button style and action
     */
    private void checkIfLoggedFollowSelectedUser() {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.FollowNode.KEY)
                .child(selectedUser.getId())
                .child(loggedUser.getId())
                .child(Constants.FollowNode.FOLLOWER)
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
     * Change behavior of app back button to just finish activity
     * We don't have a back button on screen when it's logged user profile, so this
     * method purpose is to work only on friends profiles (Other users)
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    /**
     * Config bottom navigation only if user is viewing his own profile
     * to make it  looks more like a menu
     */
    private void configBottomNavigation() {

        // Enable navigation
        enableNavigation(profileBottomNavigation);

    }
    /**
     * Method that treats click events on BottomNavigationViewEx buttons to return
     * To main activity with right id
     */
    private void enableNavigation(BottomNavigationViewEx viewEx) {

        viewEx.setSelectedItemId(R.id.icProfile);
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.icProfile)  return false;
                else {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.IntentKey.NAV_BOTTOM, item.getItemId());
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                }
            };
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Make sure to finish main activity even if it's possibly already finished
     * User may wan't to logout when inside a friend profile. As is not possible to
     * Hide drop-down menu when user is in a friend profile (and show it only on his profile,
     * We can't override this method conditionally), we need to make sure that main activity
     * Is beind finished when he loges out from there
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogOut:
                FirebaseConfig.getAuth().signOut();
                MainActivity.mainActivity.finish();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}