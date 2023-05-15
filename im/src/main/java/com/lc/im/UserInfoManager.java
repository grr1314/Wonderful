package com.lc.im;

import java.util.List;

/**
 * created by lvchao 2023/5/14
 * describe: 管理用户信息
 */
public abstract class UserInfoManager<T> {

    public abstract void setUserInfo(T t, UserInfoListener<T> listener);

    public abstract void getAllUserInfo(String[] userIds, UserInfoListener<T> listener);

    public abstract void getSingleUserInfo(String userId, UserInfoListener<T> listener, boolean async);

    public interface UserInfoListener<T> {
        void getAllUserInfo(List<T> uinfos);

        void getSingleUserInfo(T t);

        void setSingleInfoSuccess(String type);

        void setUserInfoSuccess(String success);

        void onError(String error);
    }
}
