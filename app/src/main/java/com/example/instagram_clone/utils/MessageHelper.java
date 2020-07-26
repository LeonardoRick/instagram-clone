package com.example.instagram_clone.utils;

import android.app.Activity;
import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
     * Hide loading and free user interaction
     * Must be called after openLoadingDialog() at some point
     */
    public static void closeLoadingDialog() {
        alertDialog.cancel();
    }

    /**
     * Used to build a input that can be set as view of alertDialog
     * steps:
     * 1) EditText = MessageHelper.getInputToDialog()
     * 2) FrameLayout container = new FrameLayout(context);
     *    container.addView(input);
     * 3) AlertDialog.Builder alert = new AlertDialog.Builder(this);
     *    alert.setView(container);
     * @return
     */
    public static EditText getInputToDialog() {
        // setup input
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(dpToPixel(32), 0, dpToPixel(32), 0);
        final EditText input = new EditText(context);
        input.setLayoutParams(params);
        input.setSingleLine(); // never call this method after setTransformatoinMethod
        input.setTransformationMethod(PasswordTransformationMethod.getInstance()); // show as password

        return input;
    }


    private static int dpToPixel(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density); // pixel value
    }
}
