package com.lc.im.db;

import android.content.Context;

import com.lc.im.model.UnReadImInfo;
import com.lc.repository.cache.db.Helper;

import java.util.List;

/**
 * created by lvchao 2023/5/9
 * describe:
 */
public class ImCache {
    private final ImDB database;

    public ImCache(Context context) {
        Helper dbCreateHelper = new Helper();
        database = (ImDB) dbCreateHelper.create(context, ImDB.class, "imDb");
    }

    public void addUnread(UnReadImInfo unReadImInfo) {
        database.getUnReadImInfoDao().insert(unReadImInfo);
    }

    public List<UnReadImInfo> getRunReadInfo() {
        return database.getUnReadImInfoDao().query();
    }
}
