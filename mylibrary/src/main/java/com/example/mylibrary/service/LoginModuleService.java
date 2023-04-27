package com.example.mylibrary.service;


import com.example.mylibrary.IModuleService;

public interface LoginModuleService extends IModuleService {
    //判断当前登录状态
    boolean isLogin();
}
