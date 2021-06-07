package com.leeeshuang.ad_skipper.service;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.leeeshuang.ad_skipper.dao.UsageDao;
import com.leeeshuang.ad_skipper.model.Usage;

@Database(entities = {Usage.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsageDao usageDao();
}
