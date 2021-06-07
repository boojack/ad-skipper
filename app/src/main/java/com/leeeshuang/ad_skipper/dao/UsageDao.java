package com.leeeshuang.ad_skipper.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.leeeshuang.ad_skipper.model.Usage;

import java.util.List;

@Dao
public interface UsageDao {
    @Query("SELECT * FROM usages")
    List<Usage> getAll();

    @Query("DELETE FROM usages")
    void clearAll();

    @Insert
    void insert(Usage usage);
}
