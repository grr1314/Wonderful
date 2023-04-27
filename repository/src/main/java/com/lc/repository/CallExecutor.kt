package com.lc.repository

import android.util.Log
import com.lc.repository.cache.DiskCache
import com.lc.repository.cache.MemoryCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CallExecutor {
    companion object {
        const val TAG = "CallExecutor"
    }

    var hasResult = false
    var cacheModel = CacheModel.NOCACHE
    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main)
    }

    private fun <T> response(async: Boolean, call: Call<BaseModel<T>>): Response<BaseModel<T>>? {
        var result: Response<BaseModel<T>>? = null
        hasResult = false
        if (async) {
            Log.e("CallExecutor", "async")
            call.enqueue(object : Callback<BaseModel<T>> {
                override fun onResponse(
                    call: Call<BaseModel<T>>,
                    response: Response<BaseModel<T>>
                ) {
                    result = response
                    hasResult = true
                }

                override fun onFailure(call: Call<BaseModel<T>>, t: Throwable) {
                    //处理本地的一下错误
                    //构建一个错误的response对象，或者直接返回null
                    hasResult = true
                }
            })
            while (!hasResult) {
                continue
            }//死循环阻塞等待结果
        } else {
            try {
                result = call.execute()
            } catch (e: IOException) {
                result = null
                e.printStackTrace()
            }
        }
        return result
    }

    public fun cacheModel(model: CacheModel): CallExecutor {
        cacheModel = model
        return this
    }

    public fun <T : Any> doExecute(
        name: String = "",
        async: Boolean = true,
        call: Call<BaseModel<T>>,
        resultListener: ResultListener<T>
    ) {
        var finalData: T? = null
        var cacheResult: BaseModel<T>? = null
        lateinit var errorInfo: ErrorInfo
        scope.launch {
            withContext(Dispatchers.Default) {
                val result: Response<BaseModel<T>>? = response(async, call)
                //先处理数据
                if (result?.body() != null) {
                    //处理数据
                    if (result.isSuccessful) {
                        if (result.body()?.getResultCode() == "200") {
                            if (result.body()!!.getData() == null) {
                                //数据异常
                                errorInfo = ErrorInfo(
                                    2,
                                    100001,
                                    "数据异常"
                                )
                            } else {
                                errorInfo = ErrorInfo(0, 0, "")
                                finalData = result.body()?.getData()!!
                                cacheResult = result.body()
                            }
                        } else {
                            //处理业务错误
                            val model = result.body()
                            errorInfo =
                                ErrorInfo(
                                    1,
                                    model?.getResultCode()?.toInt() ?: 0,
                                    model?.getReason()
                                )
                        }
                    } else {
                        //处理Http错误
                        errorInfo = ErrorInfo(
                            3,
                            result.code(),
                            result.message()
                        )
                    }
                } else {
                    //构建一个错误对象,定义为未知错误
                    errorInfo = ErrorInfo(
                        2,
                        100000,
                        "未知错误"
                    )
                }
                if (cacheModel > CacheModel.NOCACHE) needCache(name, cacheResult, cacheModel)
            }
            //回调
            if (errorInfo.type != 0) {
                resultListener.onError(errorInfo)
            } else {
                finalData?.let { resultListener.onSuccess(it) }
            }
        }
    }

    private fun <T> needCache(name: String, finalData: BaseModel<T>?, cacheModel: CacheModel) {
        if (finalData?.getData() == null) return
        when (cacheModel) {
            CacheModel.MEMORY -> {
                MemoryCache.getInstance().put(name, finalData)
            }
            CacheModel.DISK -> {
                DiskCache.getInstance().put(name, finalData.getData())
            }
            CacheModel.BOTH -> {
                MemoryCache.getInstance().put(name, finalData)
                DiskCache.getInstance().put(name, finalData.getData())
            }
            else -> {

            }
        }
    }

    interface ResultListener<T> {
        fun onSuccess(result: T)
        fun onError(errorInfo: ErrorInfo)
    }
}