package com.lc.repository;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseRepository<T> implements IRepository<BaseModel<T>> {

    private DataCallback<T> dataCallback;
    public static final int FROM_NET = 3;
    public static final int FROM_DISK = 2;
    public static final int FROM_MEMORY = 1;
    private int state = FROM_NET;
    private final ExecutorService single = (ExecutorService) Executors.newSingleThreadExecutor();

    public void setDataCallback(DataCallback<T> dataCallback) {
        this.dataCallback = dataCallback;
    }

    public void removeDataCallback() {
        dataCallback = null;
    }

    @Override
    public void loadData(Param param, int tag, boolean keep) {
        single.execute(() -> loadDataSync(param, tag, keep));
    }

    /**
     * 直接从网络获取
     *
     * @param param
     */
    public void loadDataWithNoCache(Param param) {
        loadData(param, FROM_NET, false);
    }

    /**
     * @param tag
     * @param keep
     */
    public synchronized void loadDataSync(Param param, int tag, boolean keep) {
        //param 表示接口的入参
        //tag 用于控制数据可以从哪一层获取
        //keep 用于判断拿到数据之后是否继续请求，例如：在Disk这层拿到了数据，但还是要用网络去拿数据
        //默认情况下直接走网络
        BaseModel<T> result;
        tag = Math.max(tag, FROM_MEMORY);
        tag = Math.min(tag, FROM_NET);
        state = tag;
        while (state <= FROM_NET) {
            switch (state) {
                case FROM_MEMORY:
                    result = loadDataFromMemory(param);
                    if (result != null) {
                        if (dataCallback != null) {
                            dataCallback.fromMemory(result.data, keep && tag != FROM_NET);
                        }
                    } else
                        //如果返回数据为null，要从下一级获取数据，这样就不受con变量控制
                        if (!keep) state++;
                    break;
                case FROM_DISK:
                    result = loadDataFromDisk(param);
                    if (result != null) {
                        if (dataCallback != null) {
                            dataCallback.fromDisk(result.data, keep && tag != FROM_NET);
                        }
                    } else
                        //如果返回数据为null，要从下一级获取数据，这样就不受con变量控制
                        if (!keep) state++;
                    break;
                case FROM_NET:
                    loadDataFromNetwork(param, api(), new CallExecutor.ResultListener<T>() {
                        @Override
                        public void onError(@NonNull ErrorInfo errorInfo) {
                            //先剥离一些通用的错误，比如一下用Toast提示的
                            //然后剩下的交给UI处理
                            if (dataCallback != null) {
                                dataCallback.onError(errorInfo);
                            }
                        }

                        @Override
                        public void onSuccess(T result) {
                            if (dataCallback != null) {
                                dataCallback.fromNet(result, true);
                            }
                        }

                    });

                    break;
            }
            if (keep) state++;
            else state = FROM_NET + 1;
        }
    }

    protected String getRepositoryName() {
        return "";
    }

    @Override
    public BaseModel<T> loadDataFromMemory(Param param) {
        return null;
    }

    @Override
    public BaseModel<T> loadDataFromDisk(Param param) {
        return null;
    }

    protected Class<?> api() {
        return Api.class;
    }

    protected abstract void loadDataFromNetwork(Param param, Class<?> api, CallExecutor.ResultListener<T> resultListener);

    /**
     * 考虑到数据可能会被返回多次，页面会多次刷新数据，所以设计了这个回调。
     *
     * @param <T>
     */
    public interface DataCallback<T> {
        void fromNet(T result, boolean dismiss);

        void fromDisk(T result, boolean dismiss);

        void fromMemory(T result, boolean dismiss);

        void onError(ErrorInfo errorInfo);
    }
}
