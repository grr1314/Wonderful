package com.lc.repository.cache.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lc.repository.cache.db.dao.CateInfoDao;
import com.lc.repository.model.MenuCateInfo;

//注解Database告诉系统这是Room数据库对象
//entities指定该数据库有哪些表，多张表就逗号分隔
//version指定数据库版本号，升级时需要用到
//数据库继承自RoomDatabase
@Database(entities = {MenuCateInfo.class}, version = 1,exportSchema = true)
public abstract class DefaultDB extends RoomDatabase {

    private static final String DATABASE_NAME = "emperor_db";

    private static DefaultDB databaseInstance;

    //结合单例模式完成创建数据库实例
    public static synchronized DefaultDB getDatabaseInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), DefaultDB.class, DATABASE_NAME).build();
        }
        return databaseInstance;
    }

    //将第四步创建的Dao对象以抽象方法的形式返回
    public abstract CateInfoDao getMenuCateInfoDao();
}
