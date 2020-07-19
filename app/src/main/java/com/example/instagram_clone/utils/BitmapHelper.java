package com.example.instagram_clone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class BitmapHelper {

    /**
     * return bipmap image from given Uri
     * @param context context
     * @param selectedImageUri uri of image to get bitMap
     * @return Bitmap image of provided uri
     */
    public static Bitmap getBitmap(Context context, Uri selectedImageUri) {
        try  {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), selectedImageUri);
                return ImageDecoder.decodeBitmap(source);
            } else
                return getDeprecatedBitMap(context, selectedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Media.getBitmap is deprecated so should only be called on android P or bellow
     * @param context context
     * @param selectedImageUri uri of image to get bitmap
     * @return Bitmap image of provided uri
     */
    private static Bitmap getDeprecatedBitMap(Context context, Uri selectedImageUri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImageUri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert Bitmap to byteArray
     * @param image bitmap
     * @return byte array of image
     */
    public static byte[] bitmapToByteArray(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // object that allows convertions to byte array
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * create a uri path from given bitmap image
     * @param context
     * @param image bitmap
     * @return uri
     */
    public static Uri getBitmapUri(Context context, Bitmap image) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "Title", null);
        return Uri.parse(path);
    }
}
