package com.lc.im.hyphenate;

import com.hyphenate.chat.EMUserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * created by lvchao 2023/5/15
 * describe:
 */
public class HyUserInfoCache {
    private final Map<String, EMUserInfo> cache=new HashMap<>();


    public Map<String, EMUserInfo> getCache() {
        return cache;
    }


}
