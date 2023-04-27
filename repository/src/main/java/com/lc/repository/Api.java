package com.lc.repository;

import com.lc.repository.model.MenuCateInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * created by lvchao 2021-01-30
 * describe:
 */
public interface Api {
    @GET("category")
        //	parentid	否	int	分类ID，默认全部
        // 	key	是	string	在个人中心->我的数据,接口名称上方查看 e5ec7ec1e01efbb633b785270735ad5f
        // 	dtype
    Call<BaseModel<List<MenuCateInfo>>> getCategory(@Query("parentid") String parentid,
                                                    @Query("key") String key,
                                                    @Query("dtype") String dtype);

}
