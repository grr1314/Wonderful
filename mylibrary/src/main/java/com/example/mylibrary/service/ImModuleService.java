package com.example.mylibrary.service;

import android.content.Context;

import com.example.mylibrary.IModuleService;
import com.example.mylibrary.callback.ImListener;

/**
 * created by lvchao 2023/5/6
 * describe:
 */
public interface ImModuleService extends IModuleService {
    public boolean imLoginState();

    public void imLogin(String token, String userId);

    public void imLogout();

    public int unReadCount(int count);

    public void addImListener(ImListener imListener);

    public void removeImListener(ImListener imListener);

}
