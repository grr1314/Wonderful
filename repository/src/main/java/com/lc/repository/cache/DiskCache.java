package com.lc.repository.cache;

import android.content.Context;

import androidx.room.RoomDatabase;

import com.example.mylibrary.ModuleServiceCenterProvider;
import com.example.mylibrary.service.RepositoryModuleService;
import com.lc.repository.BaseModel;
import com.lc.repository.cache.db.DefaultDB;
import com.lc.repository.cache.db.Helper;
import com.lc.repository.cache.db.dao.CateInfoDao;
import com.lc.repository.model.MenuCateInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiskCache {
    private static final Map<String, String> tableMap = new HashMap<>();
    private Helper dbCreateHelper;
    private DefaultDB database;

    //    static {
//        tableMap.
//    }
    private static class DiskCacheHolder {

        private static final DiskCache instance = new DiskCache();
    }

    private DiskCache() {
        dbCreateHelper = new Helper();
        createDb();
    }

    public static DiskCache getInstance() {
        return DiskCache.DiskCacheHolder.instance;
    }

    private void createDb() {
        RepositoryModuleService repositoryModuleService = (RepositoryModuleService)
                ModuleServiceCenterProvider.getInstance().getModuleServiceCenter().getService("moduleRepository");
        Context context = repositoryModuleService.getContext();
//        database=DefaultDB.getDatabaseInstance(context);
        database = (DefaultDB) dbCreateHelper.create(context, DefaultDB.class, "default");
    }

    private boolean checkDatabaseState() {
        if (database == null) createDb();
        return database.isOpen();
    }

    /**
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
//        if (checkDatabaseState()) {
        List<MenuCateInfo> list= (List<MenuCateInfo>) value;
        for (MenuCateInfo menuCateInfo:
             list) {
            database.getMenuCateInfoDao().insert(menuCateInfo);
        }

//        }
    }

    public Object getData() {
//        if (checkDatabaseState()) {
            Object obj = database.getMenuCateInfoDao().query();
            if (obj == null)
                return null;
            BaseModel<Object> baseModel = new BaseModel<>();
            baseModel.errorCode = 0;
            baseModel.reason = "";
            baseModel.resultCode = "200";
            baseModel.setData(obj);
            return baseModel;
//        }
//        return null;
    }

}
