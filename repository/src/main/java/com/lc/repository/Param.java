package com.lc.repository;

import java.util.HashMap;

public class Param {
    private HashMap<String, Object> paramMap = new HashMap<>();

    public void put(String key, Object value) {
        paramMap.put(key, value);
    }

    public Object get(String key) {
        if (paramMap == null) {
            return null;
        }
        return paramMap.get(key);
    }

    public String getStringParam(String key) {
        final Object ob = get(key);
        try {
            return (String) ob;
        } catch (ClassCastException e) {
            return null;
        }
    }
}
