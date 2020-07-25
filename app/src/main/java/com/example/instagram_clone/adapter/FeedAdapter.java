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
import com.example.instagram_clone.model.post.PostHelper;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.SquareImageView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.MemoryPolicy;
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
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, int position) {
        holder.likeButton.setEnabled(false);
        holder.likeButton.setLiked(false);
        FeedItem feedItem = postsList.get(position);
        final String loggedUserId = UserHelper.getLogged().getId();
        final User userWhoPosted = feedItem.getUserWhoPosted();
        final Post post = feedItem.getPost();

        // User info
        holder.postInfoProfileName.setText(userWhoPosted.getName());
        holder.postInfoLikes.setText(post.getLikes() + Constants.Labels.LIKES);

        if (userWhoPosted.getImagePath() != null) {
            Picasso.get()
                    .load(Uri.parse(userWhoPosted.getImagePath()))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.postInfoProfileImage);
        }
        // post info
        if (post.getDesc() != null) holder.postInfoDesc.setText(post.getDesc());
        Picasso.get()
                .load(Uri.parse(post.getImagePath()))
                .into(holder.postInfoImageView);


        // like button config
        if (post.getUsersWhoLiked() != null) {
            if (post.getUsersWhoLiked().contains(loggedUserId)) {
                holder.likeButton.setLiked(true);
            }
        }
        holder.likeButton.setEnabled(true);
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                holder.postInfoLikes.setText((post.getLikes() + 1)  + Constants.Labels.LIKES);
                post.addLike(loggedUserId);
                PostHelper.updateOnDatabase(post, userWhoPosted.getFollowersId());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                holder.postInfoLikes.setText((post.getLikes() - 1) + Constants.Labels.LIKES);
                post.removeLike(UserHelper.getLogged().getId());
                PostHelper.updateOnDatabase(post, userWhoPosted.getFollowersId());
            }
        });

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
        }
    }
}
