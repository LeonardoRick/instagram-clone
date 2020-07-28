package com.example.instagram_clone.adapter;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.PostComment;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<PostComment> comments;

    public CommentAdapter(List<PostComment> comments)  {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        return new CommentViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
        PostComment postComment = comments.get(position);

        // Recover user info to show on comment
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(postComment.getUserWhoCommentId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User userWhoComment = dataSnapshot.getValue(User.class);
                            holder.commentProfileName.setText(userWhoComment.getName());
                            if (userWhoComment.getImagePath() != null)
                                Picasso.get().load(Uri.parse(userWhoComment.getImagePath()))
                                    .into(holder.commentProfileImage);
                        } else {
                            holder.commentProfileName.setText("Usuário");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
        holder.commentContent.setText(postComment.getComment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView commentProfileImage;
        private TextView commentProfileName, commentContent;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentProfileImage = itemView.findViewById(R.id.commentProfileImage);
            commentProfileName = itemView.findViewById(R.id.commentProfileName);
            commentContent = itemView.findViewById(R.id.commentContent);
        }
    }
}