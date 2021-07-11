package com.example;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.example.picturemanagersoheib.data.database.UserDatabase;

public class PictureManagerApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        PictureManagerApplication.context = getApplicationContext();
    }




    public static Context getAppContext() {
        return PictureManagerApplication.context;
    }
}
