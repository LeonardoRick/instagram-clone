package com.example.instagram_clone.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.FeedItem;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.SquareImageView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<FeedItem> postsList;

    public FeedAdapter(List<FeedItem> postsList) {
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedItem feedItem = postsList.get(position);
        // recover userWhoPosted info
        User userWhoPosted = feedItem.getUserWhoPosted();
        holder.postInfoProfileName.setText(userWhoPosted.getName());
        if (userWhoPosted.getImagePath() != null)
            Picasso.get()
                    .load(Uri.parse(userWhoPosted.getImagePath()))
                    .into(holder.postInfoProfileImage);

        // recover post info
        Post post = feedItem.getPost();
        holder.postInfoLikes.setText(post.getLikes() + Constants.Labels.LIKES);
        if (post.getDesc() != null) holder.postInfoDesc.setText(post.getDesc());

        Picasso.get()
                .load(Uri.parse(post.getImagePath()))
                .into(holder.postInfoImageView);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }


    public class FeedViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView postInfoProfileImage;
        private TextView postInfoProfileName, postInfoLikes, postInfoDesc;
        private SquareImageView postInfoImageView;
        private LikeButton likeButton;
        private ImageView commentButton;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);

            postInfoProfileImage = itemView.findViewById(R.id.postInfoProfileImage);
            postInfoProfileName = itemView.findViewById(R.id.postInfoProfileName);
            postInfoLikes = itemView.findViewById(R.id.postInfoLikes);
            postInfoDesc = itemView.findViewById(R.id.postInfoDesc);
            postInfoImageView = itemView.findViewById(R.id.postInfoImageView);
            likeButton = itemView.findViewById(R.id.likeButton);
            commentButton = itemView.findViewById(R.id.commentButton);

            likeButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    //TODO
                }

                @Override
                public void unLiked(LikeButton likeButton) {

                }
            });
        }
    }
}
