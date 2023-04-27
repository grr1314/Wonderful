package com.lc.repository;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 负责构建Repository
 */
public class RepositoryFactory {
    private RepositoryFactory() {
    }

    private final Map<Class<?>, IRepository<?>> cache = new HashMap<>();

    public static RepositoryFactory getInstance() {
        return RepositoryFactoryHolder.factory;
    }

    private static class RepositoryFactoryHolder {
        private static final RepositoryFactory factory = new RepositoryFactory();
    }

    public void removeDataCallback(Class<?> clz) {
        BaseRepository<?> v = (BaseRepository<?>) cache.get(clz);
        if (v != null)
            v.removeDataCallback();
    }

    public void removeAllDataCallback() {
        for (Map.Entry<Class<?>, IRepository<?>> entry : cache.entrySet()) {
            BaseRepository<?> v = (BaseRepository<?>) entry.getValue();
            if (v != null)
                v.removeDataCallback();
        }
    }

    /**
     * 构建一个Repository对象
     *
     * @param clz
     * @return
     */
    public IRepository<?> create(Class<?> clz) {
        if (cache.containsKey(clz)) {
            cache.get(clz);
        }
        //反射创建对象
        IRepository<?> object = null;
        try {
            object = (IRepository<?>) clz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            Log.e("RepositoryFactory", "请检查" + clz.getName() + "的构造函数是否正确");
        }
        return object;
    }

}
