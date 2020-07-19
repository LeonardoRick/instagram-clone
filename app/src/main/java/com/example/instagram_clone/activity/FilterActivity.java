package com.example.instagram_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.instagram_clone.R;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.BitmapHelper;

public class FilterActivity extends AppCompatActivity {

    private ImageView selectedImageView;
    private Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initViewElements();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable back button
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black); // change backbutton icon
    }

    private void initViewElements() {
        selectedImageView = findViewById(R.id.selectedImageView);

        recoverSelectedImageInfo();
    }

    private void recoverSelectedImageInfo() {
        if (getIntent().getExtras() != null) {
            String imagePath = (String) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_PICTURE);
            Uri selectedImageUri  = Uri.parse(imagePath);

            image = BitmapHelper.getBitmap(this, selectedImageUri);
            selectedImageView.setImageBitmap(image);
        }
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