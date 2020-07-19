package com.example.instagram_clone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
}