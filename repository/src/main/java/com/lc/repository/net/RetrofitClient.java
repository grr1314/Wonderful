package com.lc.repository.net;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private Map<String, Retrofit> retrofitPool = new HashMap<>();

    private Retrofit newRetrofitClient(String url) {
        if (url == null && url.isEmpty())
            throw new IllegalStateException("url is null or empty please check it");
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        retrofitPool.put(url, retrofit);
        return retrofit;
    }

    private Retrofit getRetrofitClient(String url) {
        if (retrofitPool.containsKey(url)) return retrofitPool.get(url);
        return newRetrofitClient(url);
    }

    public <T> T create(final String url, final Class<T> service) {
        return getRetrofitClient(url).create(service);
    }
}
