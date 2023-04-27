package com.lc.repository;

public enum CacheModel {
    NOCACHE(0),
    MEMORY(1),
    DISK(2),
    BOTH(3);
    public int type;

    CacheModel(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
