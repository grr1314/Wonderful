package com.lc.menu.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lc.menu.CateInfoListRepository
import com.lc.repository.BaseRepository
import com.lc.repository.ErrorInfo
import com.lc.repository.Param
import com.lc.repository.RepositoryFactory
import com.lc.repository.model.MenuCateInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class MenuViewModel : ViewModel() {
    val cateInfoListLiveData: MutableLiveData<List<MenuCateInfo>> =
        MutableLiveData<List<MenuCateInfo>>()
    val netErrorLiveData: MutableLiveData<ErrorInfo> = MutableLiveData()
    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main)
    }
    private val repository: CateInfoListRepository by lazy {
        RepositoryFactory.getInstance()
            .create(CateInfoListRepository::class.java) as CateInfoListRepository
    }

    fun getCateInfo(update: Boolean) {
        scope.launch {
            //1 show loading
            //2 io
            withContext(Dispatchers.IO) {
                repository.setDataCallback(object :
                    BaseRepository.DataCallback<List<MenuCateInfo>> {
                    override fun fromNet(result: List<MenuCateInfo>?, dismiss: Boolean) {
                        Log.e("fromNet", result?.size.toString())
                        cateInfoListLiveData.postValue(result)
                    }

                    override fun fromDisk(result: List<MenuCateInfo>?, dismiss: Boolean) {
                        cateInfoListLiveData.postValue(result)
                    }

                    override fun fromMemory(result: List<MenuCateInfo>?, dismiss: Boolean) {
                        cateInfoListLiveData.postValue(result)
                    }

                    override fun onError(errorInfo: ErrorInfo?) {

                        if (errorInfo!!.type != 1) {
                            netErrorLiveData.value=errorInfo
                        }
                    }

                })
                //build param
                val param = Param()
                param.put("parentid", "")
                param.put("key", "e5ec7ec1e01efbb633b785270735ad5f")
                param.put("dtype", "")
                if (!update)
                    repository.loadData(param, BaseRepository.FROM_MEMORY, true)
                else
                    repository.loadDataWithNoCache(param)

            }
            //3 dismiss loading dialog and update ui
        }
    }
}