package com.example.instagram_clone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class GenericHelper {

    public static Bitmap getDeprecatedBitMap(Context context, Uri selectedImageUri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] bitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // object that allows convertions to byte array
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
