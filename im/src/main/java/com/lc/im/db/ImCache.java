package com.lc.im.db;

import android.content.Context;

import com.lc.im.model.HistoryImInfo;
import com.lc.im.model.UnReadImInfo;
import com.lc.repository.cache.db.Helper;

import java.util.List;

/**
 * created by lvchao 2023/5/9
 * describe:
 */
public class ImCache {
    private final ImDB database;
    private static final int PAGE_SIZE = 15;

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

    public void removeUnReadById(int id) {
        database.getUnReadImInfoDao().deleteById(id);
    }

    public List<HistoryImInfo> getHistoryInfo(int pageNumber, String window) {
        int offset = (pageNumber - 1) * PAGE_SIZE;
        if (offset < 0) offset = 0;
        return database.getHistoryRtmInfoDao().query(offset, window, PAGE_SIZE);
    }

    public void addHistory(HistoryImInfo historyImInfo) {
        database.getHistoryRtmInfoDao().insert(historyImInfo);
    }
}
