package com.lc.repository;

public interface IRepository<T> {
    T loadDataFromMemory(Param param);
    T loadDataFromDisk(Param param);
    void loadData(Param param, int tag, boolean con);
}
