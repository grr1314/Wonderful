package com.lc.repository.cache;

import android.util.LruCache;

import com.lc.repository.BaseModel;


public class MemoryCache {

    private static LruCache<String, Object> lruCache;

    private static MemoryCache memoryCache;

    private MemoryCache() {
        int max = 16;
        lruCache = new LruCache<String, Object>(max) {
            @Override
            protected int sizeOf(String key, Object value) {
                return 1;
            }
        };
    }

    private static MemoryCache Init() {
        if (memoryCache == null) {
            synchronized (MemoryCache.class) {
                memoryCache = new MemoryCache();
            }
        }
        return memoryCache;
    }

    public static MemoryCache getInstance() {
        return memoryCache == null ? Init() : memoryCache;
    }

    public void put(String key, Object value) {
        lruCache.put(key, value);
    }

    public <T> BaseModel<T> getLruCache(String key) {
        try {
            BaseModel<T> o = (BaseModel<T>) lruCache.get(key);
            return o;
        } catch (ClassCastException e) {
            lruCache.remove(key);
            return null;
        }
    }
}
