package com.lc.im.hyphenate;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;
import com.lc.im.UserInfoManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * created by lvchao 2023/5/14
 * describe:
 */
public class HyUserInfoManager extends UserInfoManager<EMUserInfo> {
    private HyImClient.HyHandler handler;
    private HyUserInfoCache hyUserInfoCache;

    public HyUserInfoManager(HyImClient.HyHandler handler, HyUserInfoCache hyUserInfoCache) {
        this.handler = handler;
        this.hyUserInfoCache = hyUserInfoCache;
    }

    private ExecutorService executorService;

    @Override
    public void setUserInfo(EMUserInfo emUserInfo, UserInfoListener<EMUserInfo> listener) {
        emUserInfo.setUserId(EMClient.getInstance().getCurrentUser());
//        emUserInfo.setNickname("easemob");
//        emUserInfo.setAvatarUrl("https://www.easemob.com");
//        emUserInfo.setBirth("2000.10.10");
//        emUserInfo.setSignature("hello world");
//        emUserInfo.setPhoneNumber("13333333333");
//        emUserInfo.setEmail("123456@qq.com");
//        emUserInfo.setGender(1);
        EMClient.getInstance().userInfoManager().updateOwnInfo(emUserInfo, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (listener != null) {
                    handler.post(() -> listener.setUserInfoSuccess(value));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (listener != null) {
                    handler.post(() -> listener.onError(errorMsg));
                }
            }
        });
    }

    public void setSingleInfo(EMUserInfo.EMUserInfoType type, String value, UserInfoListener<EMUserInfo> listener) {
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(type, value,

                new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (listener != null) {
                            handler.post(() -> listener.setSingleInfoSuccess(value));
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if (listener != null) {
                            handler.post(() -> listener.onError(errorMsg));
                        }
                    }
                });
    }

    public void setNickName(String nickName, UserInfoListener<EMUserInfo> listener) {
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.NICKNAME, nickName,

                new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (listener != null) {
                            handler.post(() -> listener.setSingleInfoSuccess(value));
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if (listener != null) {
                            handler.post(() -> listener.onError(errorMsg));
                        }
                    }
                });
    }

    public void setPhoneNumber(String phoneNumber, UserInfoListener<EMUserInfo> listener) {
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.PHONE, phoneNumber,

                new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (listener != null) {
                            handler.post(() -> listener.setSingleInfoSuccess(value));
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if (listener != null) {
                            handler.post(() -> listener.onError(errorMsg));
                        }
                    }
                });
    }

    public void setSignature(String signature, UserInfoListener<EMUserInfo> listener) {
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.SIGN, signature,

                new EMValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (listener != null) {
                            handler.post(() -> listener.setSingleInfoSuccess(value));
                        }
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        if (listener != null) {
                            handler.post(() -> listener.onError(errorMsg));
                        }
                    }
                });
    }

    public void setUserIcon(String url, UserInfoListener<EMUserInfo> listener) {
        EMClient.getInstance().userInfoManager().updateOwnInfoByAttribute(EMUserInfo.EMUserInfoType.AVATAR_URL, url, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String value) {
                if (listener != null) {
                    handler.post(() -> listener.setSingleInfoSuccess(value));
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if (listener != null) {
                    handler.post(() -> listener.onError(errorMsg));
                }
            }
        });
    }


    @Override
    public void getSingleUserInfo(String userId, UserInfoListener<EMUserInfo> listener, boolean async) {
        String[] userIds = new String[1];
        if (userId == null || userId.equals("")) {
            userIds[0] = EMClient.getInstance().getCurrentUser();
        } else userIds[0] = userId;
        if (hyUserInfoCache.getCache().containsKey(userId)) {
            if (!async) {
                handler.post(() -> listener.getSingleUserInfo(hyUserInfoCache.getCache().get(userId)));
            } else {
                listener.getSingleUserInfo(hyUserInfoCache.getCache().get(userId));
            }
        }
        EMUserInfo.EMUserInfoType[] userInfoTypes = new EMUserInfo.EMUserInfoType[2];
        userInfoTypes[0] = EMUserInfo.EMUserInfoType.NICKNAME;
        userInfoTypes[1] = EMUserInfo.EMUserInfoType.AVATAR_URL;
        EMClient.getInstance().userInfoManager().fetchUserInfoByAttribute(userIds, userInfoTypes, new EMValueCallBack<Map<String, EMUserInfo>>() {

            @Override
            public void onSuccess(Map<String, EMUserInfo> stringEMUserInfoMap) {
                List<EMUserInfo> userInfoList = new ArrayList<>();
                for (Map.Entry<String, EMUserInfo> en : stringEMUserInfoMap.entrySet()) {
                    userInfoList.add(en.getValue());
                }
                hyUserInfoCache.getCache().put(userId, userInfoList.get(0));
                if (listener != null) {
                    if (!async) {
                        handler.post(() -> listener.getSingleUserInfo(userInfoList.get(0)));
                    } else {
                        listener.getSingleUserInfo(userInfoList.get(0));
                    }

                }
            }

            @Override
            public void onError(int i, String errorString) {
                if (listener != null) {
                    if (!async) {
                        handler.post(() -> listener.onError(errorString));
                    } else {
                        listener.onError(errorString);
                    }
                }
            }
        });

    }

    @Override
    public void getAllUserInfo(String[] userIds, UserInfoManager.UserInfoListener listener) {
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(userIds, new EMValueCallBack<Map<String, EMUserInfo>>() {

            @Override
            public void onSuccess(Map<String, EMUserInfo> stringEMUserInfoMap) {
                List<EMUserInfo> userInfoList = new ArrayList<>();
                for (Map.Entry<String, EMUserInfo> en : stringEMUserInfoMap.entrySet()) {
                    userInfoList.add(en.getValue());
                }
                if (listener != null) listener.getAllUserInfo(userInfoList);
            }

            @Override
            public void onError(int i, String errorString) {
                if (listener != null) listener.onError(errorString);
            }
        });
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
