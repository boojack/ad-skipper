package com.leeeshuang.ad_skipper.service;

import android.content.Context;

import androidx.room.Room;

public class DatabaseService {
    public static AppDatabase db;
    public static String blackPkgNames;
    public static String skipKeyWords;
    public static boolean showSkipTip = true;

    public static void create(final Context context) {
        db = Room.databaseBuilder(
                context,
                AppDatabase.class,
                "ad-skipper")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }
}
