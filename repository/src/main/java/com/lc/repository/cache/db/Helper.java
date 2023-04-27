package com.lc.repository.cache.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

public class Helper {
    public <T extends RoomDatabase> RoomDatabase create(Context context, Class<T> clz, String dbName) {
       return Room.databaseBuilder(context, clz, dbName).build();
    }
}
