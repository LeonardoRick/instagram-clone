package com.example.instagram_clone.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.instagram_clone.CustomApplication;

public class MessageHelper {


    public static Context context  = CustomApplication.getContext();


    public static void showLongToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
