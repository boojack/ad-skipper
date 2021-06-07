package com.leeeshuang.ad_skipper.service;

import android.content.Context;

import androidx.room.Room;

import com.leeeshuang.ad_skipper.model.Usage;

import java.util.List;

public class DatabaseService {
    public static AppDatabase db;
    public static List<Usage> usageLogs;
    public static String blackPkgNames;
    public static String skipKeyWords;

    public static AppDatabase create(final Context context) {
        db = Room.databaseBuilder(
                context,
                AppDatabase.class,
                "ad-skipper")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        return db;
    }

    public static void insertUsage(Usage usageLog) {
        db.usageDao().insert(usageLog);
    }

    public static List<Usage> getUsages() {
        return usageLogs;
    }

    public static void clearAll() {
        db.usageDao().clearAll();
    }
}
