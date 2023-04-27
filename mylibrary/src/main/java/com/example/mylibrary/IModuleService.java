package com.example.mylibrary;

import android.content.Context;

public interface IModuleService {
    void attachContext(Context context);
    Context getContext();
}
