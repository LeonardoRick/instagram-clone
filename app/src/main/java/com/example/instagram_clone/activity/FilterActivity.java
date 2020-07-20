package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.adapter.ThumbnailAdapter;
import com.example.instagram_clone.model.post.Post;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.BitmapHelper;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.example.instagram_clone.utils.RecyclerItemClickListener;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    static { System.loadLibrary("NativeImageProcessor"); }

    private Bitmap image;
    private Bitmap filteredImage;
    private List<ThumbnailItem> thumbnailsList = new ArrayList<>();

    private RecyclerView recyclerFilters;
    private ThumbnailAdapter adapter;

    private ImageView selectedImageView;
    private EditText imageDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change backbutton icon

        initViewElements();
    }

    private void initViewElements() {
        selectedImageView = findViewById(R.id.selectedImageView);
        imageDesc = findViewById(R.id.textFilterDescription);
        configRecyclerView();
        recoverSelectedImageInfo();
    }

    private void recoverSelectedImageInfo() {
        if (getIntent().getExtras() != null) {
            String imagePath = (String) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_PICTURE);
            Uri selectedImageUri  = Uri.parse(imagePath);

            image = BitmapHelper.getBitmap(this, selectedImageUri);
            image = image.copy(Bitmap.Config.ARGB_8888, true);
            filteredImage = image.copy(Bitmap.Config.ARGB_8888, true);
            selectedImageView.setImageBitmap(image);

            recoverFiltersList();
        }
    }

    public void recoverFiltersList() {
        ThumbnailsManager.clearThumbs();
        thumbnailsList.clear();

        // Config default template image filter
        ThumbnailItem noFilterItem = new ThumbnailItem();
        noFilterItem.image = image;
        noFilterItem.filterName = "Sem Filtro";
        ThumbnailsManager.addThumb(noFilterItem);

        // List all library filters
        List<Filter> filters = FilterPack.getFilterPack(this);
        for (Filter filter : filters) {
            ThumbnailItem item = new ThumbnailItem();
            item.image = image;
            item.filterName = filter.getName();
            item.filter = filter;

            ThumbnailsManager.addThumb(item);
        }

        thumbnailsList.addAll(ThumbnailsManager.processThumbs(this));
        adapter.notifyDataSetChanged();
    }


    private void configRecyclerView() {
        recyclerFilters = findViewById(R.id.recyclerFilters);
        recyclerFilters.setHasFixedSize(true);
        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerFilters.setLayoutManager(layoutManager);
        // Adapter
        adapter = new ThumbnailAdapter(thumbnailsList);
        recyclerFilters.setAdapter(adapter);
        setRecyclerViewListener();
    }

    private void setRecyclerViewListener() {
        recyclerFilters.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerFilters,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                filteredImage = image.copy(Bitmap.Config.ARGB_8888, true);
                                Filter filter = thumbnailsList.get(position).filter;
                                selectedImageView.setImageBitmap(filter.processFilter(filteredImage));
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    /**
     * Method called when user clicks "check" button to post image
     */
    private void publishPost() {
        String loggedUserId = UserHelper.getLogged().getId();
        FirebaseConfig.getFirebaseStorage()
                .child(Constants.Storage.IMAGES)
                .child(Constants.Storage.POST)
                .child(loggedUserId);

        Post post = new Post();
        post.setUserId(loggedUserId);
        post.setPicturePath("");
        if (imageDesc.getText() != null)
            post.setDesc(imageDesc.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_save_post:
                publishPost();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}