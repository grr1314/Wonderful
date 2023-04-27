package com.lc.repository.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity(tableName = "cate_info_list_table")
public class MenuCateInfo {
    //PrimaryKey主键，autoGenerate自增长
    @PrimaryKey(autoGenerate = true)
    //ColumnInfo用于指定该字段存储在表中的名字，并指定类型
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    public int id;

    @SerializedName("name")
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
