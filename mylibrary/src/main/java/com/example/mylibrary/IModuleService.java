package com.example.mylibrary;

import android.content.Context;

public interface IModuleService {
    default void attachContext(Context context) {

    }

    Context getContext();
}
