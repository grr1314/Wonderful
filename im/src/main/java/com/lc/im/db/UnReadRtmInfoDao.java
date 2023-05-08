package com.lc.im.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.lc.im.model.ImInfo;
import com.lc.im.model.UnReadImInfo;

import java.util.List;

/**
 * created by lvchao 2023/5/8
 * describe:
 */
@Dao
interface UnReadRtmInfoDao {
    //插入数据
    @Insert
    public void insert(UnReadImInfo... imInfos);


    //删除数据
    @Delete
    public void delete(UnReadImInfo... imInfos);



    //不设置条件，查询所有数据
    @Query("SELECt * FROM rtm_info_unread_list_table")
    public List<UnReadImInfo> query();
}
