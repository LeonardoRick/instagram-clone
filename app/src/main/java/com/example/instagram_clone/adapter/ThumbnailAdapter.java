package com.example.instagram_clone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.utils.SquareImageView;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailsViewHolder> {

    private List<ThumbnailItem> thumbnailsList;

    public ThumbnailAdapter(List<ThumbnailItem> thumbnailsList) {
        this.thumbnailsList = thumbnailsList;
    }

    @NonNull
    @Override
    public ThumbnailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnails_list_item, parent, false);
        return new ThumbnailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailsViewHolder holder, int position) {
        ThumbnailItem item = thumbnailsList.get(position);
        holder.filterName.setText(item.filterName);
        holder.thumbNail.setImageBitmap(item.image);
    }

    @Override
    public int getItemCount() {
        return thumbnailsList.size();
    }

    public class ThumbnailsViewHolder extends RecyclerView.ViewHolder {

        private TextView filterName;
        private ImageView thumbNail;

        public ThumbnailsViewHolder(@NonNull View itemView) {
            super(itemView);

            filterName = itemView.findViewById(R.id.filterName);
            thumbNail = itemView.findViewById(R.id.thumbnailImage);
        }
    }
}