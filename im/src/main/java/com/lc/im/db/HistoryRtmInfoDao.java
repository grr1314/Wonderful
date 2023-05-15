package com.lc.im.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.lc.im.model.HistoryImInfo;
import com.lc.im.model.UnReadImInfo;

import java.util.List;

/**
 * created by lvchao 2023/5/10
 * describe:
 */
@Dao
interface HistoryRtmInfoDao {
    //插入数据
    @Insert
    public void insert(HistoryImInfo... imInfos);


    //删除数据
    @Delete
    public void delete(HistoryImInfo... imInfos);

    @Query("DELETE FROM rtm_info_history_list_table WHERE id = :msgId")
    public void deleteById(int msgId);

    /**
     *
     * @param offset
     * @param window
     * @param pageSize
     * @return
     */
    //分页查询 offset代表从第几条记录“之后“开始查询，limit表明查询多少条结果
    @Query("SELECT * FROM rtm_info_history_list_table WHERE window =:window limit :pageSize offset :offset")
    public List<HistoryImInfo> query(int offset, String window, int pageSize);

    //不设置条件，查询所有数据
    @Query("SELECt * FROM rtm_info_history_list_table")
    public List<HistoryImInfo> queryAll();
}
