package com.lc.menu;

import androidx.annotation.NonNull;

import com.lc.repository.Api;
import com.lc.repository.BaseModel;
import com.lc.repository.BaseRepository;
import com.lc.repository.CacheModel;
import com.lc.repository.CallExecutor;
import com.lc.repository.ErrorInfo;
import com.lc.repository.Param;
import com.lc.repository.cache.DiskCache;
import com.lc.repository.cache.MemoryCache;
import com.lc.repository.model.MenuCateInfo;
import com.lc.repository.net.RetrofitClient;

import java.util.List;

import retrofit2.Call;


public class CateInfoListRepository extends BaseRepository<List<MenuCateInfo>> {
    @Override
    protected String getRepositoryName() {
        return "CateInfoList";
    }

    @Override
    protected void loadDataFromNetwork(Param param, Class<?> api, CallExecutor.ResultListener<List<MenuCateInfo>> resultListener) {
        Api a = (Api) new RetrofitClient().create("https://apis.juhe.cn/cook/", api);
        Call<BaseModel<List<MenuCateInfo>>> call = a.getCategory(param.getStringParam("parentid"), param.getStringParam("key"), param.getStringParam("dtype"));
        new CallExecutor().cacheModel(CacheModel.DISK).doExecute(getRepositoryName(), true, call, new CallExecutor.ResultListener<List<MenuCateInfo>>() {
            @Override
            public void onError(@NonNull ErrorInfo errorInfo) {
                resultListener.onError(errorInfo);
            }

            @Override
            public void onSuccess(List<MenuCateInfo> result) {
                resultListener.onSuccess(result);
            }
        });
    }

    @Override
    protected Class<?> api() {
        return super.api();
    }

    @Override
    public BaseModel<List<MenuCateInfo>> loadDataFromDisk(Param param) {
        return (BaseModel<List<MenuCateInfo>>) DiskCache.getInstance().getData();
    }

    @Override
    public BaseModel<List<MenuCateInfo>> loadDataFromMemory(Param param) {
        BaseModel<List<MenuCateInfo>> result = MemoryCache.getInstance().getLruCache(getRepositoryName());
        return (BaseModel<List<MenuCateInfo>>) result;
    }
}
