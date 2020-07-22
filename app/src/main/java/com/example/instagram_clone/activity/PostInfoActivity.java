package com.example.instagram_clone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.SquareImageView;
import com.squareup.picasso.Picasso;

public class PostInfoActivity extends AppCompatActivity {

    private CircleImageView postInfoProfileImage;
    private TextView postInfoProfileName, postInfoLikes, postInfoDesc;
    private SquareImageView postInfoImageView;

    private User selectedUser;
    private Post selectedPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        // Config Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button;
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // Changes back button icon

        initViewElements();
    }

    private void initViewElements() {
        postInfoProfileImage = findViewById(R.id.postInfoProfileImage);
        postInfoProfileName = findViewById(R.id.postInfoProfileName);
        postInfoLikes = findViewById(R.id.postInfoLikes);
        postInfoDesc = findViewById(R.id.postInfoDesc);
        postInfoImageView = findViewById(R.id.postInfoImageView);

        recoverIntentInfo();
    }

    private void recoverIntentInfo() {
        if (getIntent().getExtras() != null) {
            selectedUser = (User) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_USER);
            selectedPost = (Post) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_POST);

            // Recover user info
            postInfoProfileName.setText(selectedUser.getName());
            if (selectedUser.getImagePath() != null)
                Picasso.get().load(Uri.parse(selectedUser.getImagePath()))
                        .into(postInfoProfileImage);

            // Recover post info
            postInfoDesc.setText(selectedPost.getDesc());
            Picasso.get().load(Uri.parse(selectedPost.getImagePath()))
                    .into(postInfoImageView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}