package com.lc.repository.cache.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lc.repository.model.MenuCateInfo;

import java.util.List;

@Dao
public interface CateInfoDao {
    //插入数据
    @Insert
    public void insert(MenuCateInfo... cateInfos);


    //删除数据
    @Delete
    public void delete(MenuCateInfo... cateInfos);


    //修改数据
    @Update
    public void update(MenuCateInfo... cateInfos);

    //不设置条件，查询所有数据
    @Query("SELECt * FROM cate_info_list_table")
    public List<MenuCateInfo> query();
}
