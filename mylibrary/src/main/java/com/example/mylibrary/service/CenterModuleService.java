package com.example.mylibrary.service;

import com.example.mylibrary.IModuleService;

import java.util.concurrent.ExecutorService;

/**
 * created by lvchao 2023/5/12
 * describe:
 */
public interface CenterModuleService extends IModuleService {
    public ExecutorService globalExecutorService();
}
