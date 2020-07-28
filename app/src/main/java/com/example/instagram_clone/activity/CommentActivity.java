package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.CommentAdapter;
import com.example.instagram_clone.model.PostComment;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.MessageHelper;
import com.example.instagram_clone.utils.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class CommentActivity extends AppCompatActivity {

    private CommentAdapter adapter;
    private DatabaseReference postCommentsRef;
    private ValueEventListener commentsEventListener;
    private User loggedUser;
    private User postOwner;

    private RecyclerView recyclerViewComments;
    private EditText editTextNewComment;
    private Post selectedPost;

    private boolean addCommentAllowed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comentários");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change back button icon

        loggedUser = UserHelper.getLogged();
        initViewElements();
    }

    @Override
    protected void onStart() {
        super.onStart();
        commentEventListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (commentsEventListener != null)
            postCommentsRef.removeEventListener(commentsEventListener);
    }

    private void initViewElements() {
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        editTextNewComment = findViewById(R.id.editTextNewComment);
        recoverSelectedPostInfo();
        recoverPostOwnerInfo();
    }

    private void recoverSelectedPostInfo() {
        if (getIntent().getExtras() != null) {
            selectedPost = (Post) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_POST);
            configRecyclerView();
        }
    }

    /**
     * Recover owner of post, so we can update comment on every follower feed
     */
    private void recoverPostOwnerInfo() {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(selectedPost.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())  {
                            postOwner = dataSnapshot.getValue(User.class);
                            addCommentAllowed = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void configRecyclerView() {
        recyclerViewComments.setHasFixedSize(true);
        // Layout Manager
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        // Adapter
        adapter = new CommentAdapter(selectedPost.getPostComments());
        recyclerViewComments.setAdapter(adapter);
        setDeleteCommentListener();
    }

    /**
     * Called to add comment when user clicks on send button
     */
    public void addComment(View view) {
        if (editTextNewComment.getText() != null && !editTextNewComment.getText().toString().trim().equals("")) {

            if (addCommentAllowed) {
                String comment = editTextNewComment.getText().toString();
                PostComment postComment = new PostComment(loggedUser.getId(), comment);
                selectedPost.addComment(postComment);
                PostHelper.updateOnDatabase(selectedPost, postOwner.getFollowersId());

                editTextNewComment.setText("");
            } else
                MessageHelper.showLongToast("Aguarde as informações carregarem e tente novamente");

        }
    }

    /**
     * called to listen to comments node of this post on database
     * and update list when anybody add a new comment
     */
    public void commentEventListener() {
        postCommentsRef =
                FirebaseConfig.getFirebaseDatabase()
                .child(Constants.PostNode.KEY)
                .child(selectedPost.getUserId())
                .child(selectedPost.getId())
                .child(Constants.PostNode.POST_COMMENTS);

        commentsEventListener =
                postCommentsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        selectedPost.getPostComments().clear();
                        for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                            selectedPost.addComment(commentSnapshot.getValue(PostComment.class));
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }


    /**
     * listener to remove comment clicking and holding on it
     */

    private void setDeleteCommentListener() {
        recyclerViewComments.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewComments,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                final PostComment comment = selectedPost.getPostComments().get(position);
                                // if user commented or is the post owner, he can delete posts
                                if (comment.getUserWhoCommentId().equals(loggedUser.getId())
                                        || selectedPost.getUserId().equals(loggedUser.getId())) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(CommentActivity.this);
                                    alert.setTitle("Certeza que deseja remover o comentário?");
                                    alert.setNegativeButton("Cancelar", null);
                                    alert.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            selectedPost.removeComment(comment);
                                            PostHelper.updateOnDatabase(selectedPost, postOwner.getFollowersId());
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    alert.create();
                                    alert.show();
                                }

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}