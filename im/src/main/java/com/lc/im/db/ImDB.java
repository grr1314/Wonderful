package com.lc.im.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lc.im.model.UnReadImInfo;

/**
 * created by lvchao 2023/5/9
 * describe:
 */
@Database(entities = {UnReadImInfo.class}, version = 1,exportSchema = true)
public abstract class ImDB extends RoomDatabase {


    public abstract UnReadRtmInfoDao getUnReadImInfoDao();
}
