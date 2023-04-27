package com.example.myapplication.bus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件总线
 */
public class LiveDataBus {
    private final HashMap<String, BusMutableLiveData<Object>> busMap;

    private LiveDataBus() {
        busMap = new HashMap<>();
    }

    public static LiveDataBus getInstance() {
        return BusHolder.liveDataBus;
    }

    private void addChannel(String channelName) {
        if (!busMap.containsKey(channelName)) {
            busMap.put(channelName, new BusMutableLiveData<>());
        }
    }

    public void removeChannel(String channelName) {
        BusMutableLiveData<Object> busMutableLiveData = busMap.remove(channelName);
        if (busMutableLiveData != null)
            busMutableLiveData.removeAllBusObservers();
    }

    public <T> BusMutableLiveData<T> getChannel(String channelName, Class<T> type) {
        addChannel(channelName);
        return (BusMutableLiveData<T>) busMap.get(channelName);
    }

    public <T> BusMutableLiveData<T> getChannel(String channelName) {
        return (BusMutableLiveData<T>) getChannel(channelName, Object.class);
    }

    public <T> LiveDataBus observe(LifecycleOwner owner, Class<T> type, String channelName, Observer<T> observer) {
        getChannel(channelName, type).observe(owner, (Observer<Object>) o -> observer.onChanged((T) o));
        return getInstance();
    }

    private static class BusHolder {
        private static final LiveDataBus liveDataBus = new LiveDataBus();
    }

    /**
     * 处理数据倒灌问题
     *
     * @param <T>
     */
    private static class BusObserver<T> implements Observer<T> {
        private boolean pending = false;
        private final Observer<? super T> obs;

        public BusObserver(Observer<? super T> observer) {
            this.obs = observer;
        }

        @Override
        public void onChanged(T t) {
            if (!pending) {
                obs.onChanged(t);
            }
        }

        public void setPending(boolean pending) {
            this.pending = pending;
        }
    }

    public static class BusMutableLiveData<T> extends MutableLiveData<T> {
        private final HashMap<Observer<? super T>, BusObserver<T>> busObserverHashMap = new HashMap<>();

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            checkObserver(observer);
            BusObserver<T> busObserver = busObserverHashMap.get(observer);
            if (busObserver == null) return;
            super.observe(owner, busObserver);
            hook(busObserver);
        }

        private void checkObserver(Observer<? super T> observer) {
            if (!busObserverHashMap.containsKey(observer))
                busObserverHashMap.put(observer, new BusObserver<>(observer));
        }

        public void setValue(@Nullable T t) {
            for (BusObserver<T> busObserver : busObserverHashMap.values()) {
                busObserver.setPending(false);
            }
            super.setValue(t);
        }

        public void removeAllBusObservers() {
            busObserverHashMap.clear();
        }

        /**
         * 处理粘性事件问题
         *
         * @param observer
         */
        private void hook(Observer<? super T> observer) {
            Class<LiveData> liveClz = LiveData.class;
            try {
                //获取ObserverWrapper的mLastVersion
                Field fieldObservers = liveClz.getDeclaredField("mObservers");
                fieldObservers.setAccessible(true);
                Object objectObserver = fieldObservers.get(this);
                assert objectObserver != null;
                Class<?> objectClz = objectObserver.getClass();
                Method methodGet = objectClz.getDeclaredMethod("get", Object.class);
                methodGet.setAccessible(true);
                Object objectWrapperEntry = methodGet.invoke(objectObserver, observer);
                Object objectWrapper = null;
                if (objectWrapperEntry instanceof Map.Entry) {
                    objectWrapper = ((Map.Entry<?, ?>) objectWrapperEntry).getValue();
                }
                if (objectWrapper == null) {
                    throw new IllegalArgumentException("entry can not be null");
                }
                Class<?> targetClass = objectWrapper.getClass();
                Class<?> targetSuperClass = targetClass.getSuperclass();
                Field fieldLastVersion = targetSuperClass.getDeclaredField("mLastVersion");
                fieldLastVersion.setAccessible(true);
                //获取LiveData的mVersion
                Field fieldVersion = liveClz.getDeclaredField("mVersion");
                fieldVersion.setAccessible(true);
                Object ob = fieldVersion.get(this);
                //设置数值
                fieldLastVersion.set(objectWrapper, ob);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
