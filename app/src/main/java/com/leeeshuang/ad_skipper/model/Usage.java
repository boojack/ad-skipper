package com.leeeshuang.ad_skipper.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usages")
public class Usage {
    @PrimaryKey(autoGenerate = true)
    public int id = 0;
    // 应用名称
    @ColumnInfo(name = "name")
    public String name;
    // 应用名称
    @ColumnInfo(name = "pkg_name")
    public String pkgName;
    // 上次跳过的时间
    @ColumnInfo(name = "last_skipped_at")
    public long lastSkippedAt;

    public Usage(String name, String pkgName, long lastSkippedAt) {
        this.name = name;
        this.pkgName = pkgName;
        this.lastSkippedAt = lastSkippedAt;
    }
}
