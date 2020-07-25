package com.example.instagram_clone.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.instagram_clone.CustomApplication;
import com.example.instagram_clone.R;

import androidx.appcompat.app.AlertDialog;

public class MessageHelper {


    public static Context context  = CustomApplication.getContext();

    private static AlertDialog alertDialog;


    public static void showLongToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * Show loading and block user interaction
     * Must be called with closeLoadingDialog() at some point
     */
    public static void openLoadingDialog(Activity activity, String title) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(title);
        alert.setCancelable(false);
        alert.setView(R.layout.loading);
        alertDialog = alert.create();
        alertDialog.show();
    }

    /**
     * hide loading and free user interaction
     * Must be called after openLoadingDialog() at some point
     */
    public static void closeLoadingDialog() {
        alertDialog.cancel();
    }
}
