package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.SquareImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

public class PostInfoActivity extends AppCompatActivity {

    private CircleImageView postInfoProfileImage;
    private TextView postInfoProfileName, postInfoLikes, postInfoDesc;
    private SquareImageView postInfoImageView;
    private LikeButton likeButton;
    private ImageView commentButton;

    private User selectedUser;
    private User loggedUser;
    private Post selectedPost;

    private DatabaseReference postRef;
    private ValueEventListener valueEventListener;

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
        loggedUser = UserHelper.getLogged();
        initViewElements();
    }

    @Override
    protected void onStart() {
        super.onStart();
        postListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null) postRef.removeEventListener(valueEventListener);
    }

    private void initViewElements() {
        postInfoProfileImage = findViewById(R.id.postInfoProfileImage);
        postInfoProfileName = findViewById(R.id.postInfoProfileName);
        postInfoLikes = findViewById(R.id.postInfoLikes);
        postInfoDesc = findViewById(R.id.postInfoDesc);
        postInfoImageView = findViewById(R.id.postInfoImageView);

        likeButton = findViewById(R.id.likeButton);
        likeButton.setEnabled(false);
        commentButton = findViewById(R.id.commentButton);

        recoverIntentInfo();
        setLikeListener();
        setCommentListener();
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

            postInfoLikes.setText(selectedPost.getLikes() + Constants.Labels.LIKES);
        }
    }

    private void setLikeListener() {
        if (selectedPost.getUsersWhoLiked() != null) {
            if (selectedPost.getUsersWhoLiked().contains(loggedUser.getId())) {
                likeButton.setLiked(true);
            }
        }
        likeButton.setEnabled(true);
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                selectedPost.addLike(loggedUser.getId());
                PostHelper.updateOnDatabase(selectedPost, selectedUser.getFollowersId());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                selectedPost.removeLike(loggedUser.getId());
                PostHelper.updateOnDatabase(selectedPost, selectedUser.getFollowersId());
            }
        });
    }

    private void setCommentListener() {
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra(Constants.IntentKey.SELECTED_POST, selectedPost);
                startActivity(intent);
            }
        });
    }

    /**
     * listener to update on realtime posts
     */
    private void postListener() {
        postRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PostNode.KEY)
                .child(selectedUser.getId())
                .child(selectedPost.getId());

        valueEventListener = postRef
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        selectedPost = dataSnapshot.getValue(Post.class);
                        postInfoLikes.setText(selectedPost.getLikes() + Constants.Labels.LIKES);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}