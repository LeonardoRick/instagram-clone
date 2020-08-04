package com.example.instagram_clone;

import android.app.Application;
import android.content.Context;

public class CustomApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

}
