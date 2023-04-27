package com.lc.nativelib.configs;

import com.lc.nativelib.listener.IConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置的集合
 */
public class Config {
    private Map<Class<?>, IConfig> configMap = new HashMap<>();

    public void add(IConfig config) {
        configMap.put(config.getClass(), config);
    }

    public Map<Class<?>, IConfig> getConfigMap() {
        return configMap;
    }
}
