package com.leeeshuang.ad_skipper.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.room.Room;

import com.leeeshuang.ad_skipper.model.Usage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leeeshuang.ad_skipper.MainActivity.blackPkgNameKey;
import static com.leeeshuang.ad_skipper.MainActivity.preferences;

public class DatabaseService {
    public static AppDatabase db;
    public static List<Usage> usageLogs;
    public static String blackPkgNames;
    public static Map<String, String> blackPkgNameMap = new HashMap<String, String>();
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

    public static void setBlackPkgNames(String blackPkgNames) {
        DatabaseService.blackPkgNames = blackPkgNames;
        for (String pkgString : blackPkgNames.split(" ")) {
            if (!TextUtils.isEmpty(pkgString)) {
                String[] rawValue = pkgString.split(":");

                if (rawValue.length > 0) {
                    String key = rawValue[0];
                    String values = "";

                    if (rawValue.length == 2) {
                        values = rawValue[1];
                    }

                    DatabaseService.blackPkgNameMap.put(key, values);
                }
            }
        }
    }

    public static void updateBlackPkgNames() {
        // 保存键值对
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = preferences.edit();
        List<String> temp = new ArrayList<String>();
        DatabaseService.blackPkgNameMap.entrySet().forEach(entry -> {
            temp.add(entry.getKey() + ":" + entry.getValue());
        });
        editor.putString(blackPkgNameKey, String.join(" ", temp));
        editor.apply();
    }
}
