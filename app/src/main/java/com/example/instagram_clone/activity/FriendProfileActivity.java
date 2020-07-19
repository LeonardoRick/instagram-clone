package com.example.instagram_clone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.utils.Constants;
import com.squareup.picasso.Picasso;

public class FriendProfileActivity extends AppCompatActivity {

    private User selectedUser;

    private Button buttonFollow;
    private CircleImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        // Config Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change icon of back button

        initViewElements();
        recoverSelectedUserInfo();

    }

    private void initViewElements() {
        profileImage = findViewById(R.id.circleImageViewProfile);
        buttonFollow = findViewById(R.id.profileActionButton);
        buttonFollow.setText("Seguir");
        buttonFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * recover selected user info to build his profile page
     */
    private void recoverSelectedUserInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle.containsKey(Constants.Intent.SELECTED_USER)) {
            selectedUser = (User) bundle.getSerializable(Constants.Intent.SELECTED_USER);
            getSupportActionBar().setTitle(selectedUser.getName());
            if (selectedUser.getPicturePath() != null) {
                Uri uri = Uri.parse(selectedUser.getPicturePath());
                Picasso.get().load(uri)
                        .into(profileImage);
            } else
                profileImage.setImageResource(R.drawable.profile);
        }
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