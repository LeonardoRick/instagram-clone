package com.example.instagram_clone.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.user.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> usersList;

    public UserAdapter(List<User> usersList) {
        this.usersList = usersList;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        return new UserViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.name.setText(user.getName());

        if (user.getPicturePath() != null) {
            Uri uri = Uri.parse(user.getPicturePath());
            Picasso.get().load(uri)
                    .placeholder(R.drawable.profile)
                    .into(holder.profileImage);
        } else
            holder.profileImage.setImageResource(R.drawable.profile);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CircleImageView profileImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewUserItemName);
            profileImage = itemView.findViewById(R.id.circleImageViewUserItemImage);
        }
    }
}