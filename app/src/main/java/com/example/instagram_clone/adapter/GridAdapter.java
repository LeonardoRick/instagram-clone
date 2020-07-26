package com.example.instagram_clone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.instagram_clone.R;
import com.example.instagram_clone.model.post.Post;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GridAdapter extends ArrayAdapter<Post> {
    private int layoutResource;

    public GridAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final GridViewHolder gridViewHolder;
        if (convertView == null) {
            gridViewHolder = new GridViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            gridViewHolder.image = convertView.findViewById(R.id.imageViewPost);
            gridViewHolder.progressBar = convertView.findViewById(R.id.progressBarGridPost);

            convertView.setTag(gridViewHolder);
        } else
            gridViewHolder = (GridViewHolder) convertView.getTag();

        Post post = getItem(position);
        String imagePath = post.getImagePath();

        gridViewHolder.progressBar.setVisibility(View.VISIBLE);
        Uri uri = Uri.parse(imagePath);
        Picasso.get().load(uri)
                .into(gridViewHolder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        gridViewHolder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        gridViewHolder.progressBar.setVisibility(View.GONE);
                    }
                });
        return convertView;
    }

    public class GridViewHolder {
        private ImageView image;
        private ProgressBar progressBar;
    }

    @Nullable
    @Override
    public Post getItem(int position) {
        return super.getItem(position);
    }
}