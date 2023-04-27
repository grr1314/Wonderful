package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.center.CenterPlugin
import com.example.myapplication.activity.BookManagerActivity
import com.example.myapplication.activity.LiveDataActivity
import com.example.myapplication.activity.PagingActivity
import com.example.myapplication.activity.ViewActivity
import com.example.myapplication.adapter.HomeRecyclerAdapter
import com.example.myapplication.delegate.*
import com.example.myapplication.model.HomeItemData
import com.example.myapplication.view.MyView
import com.example.mylibrary.ModuleInfoTable
import com.example.mylibrary.service.LoginModuleService
import com.lc.nativelib.AppMonitor
import com.lc.nativelib.configs.AnrConfig
import com.lc.nativelib.configs.Config
import com.lc.nativelib.display.MonitorDisplayActivity
import com.lc.nativelib.listener.AnrListener
import com.lc.nativelib.model.MessageInfo
import com.lc.nativelib.window.FloatClient
import com.lc.nativelib.window.IFloatPermissionCallback
import com.lc.routerlib.core.ZRouter
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


class MainActivity : AppCompatActivity() {
    private lateinit var TAG: String
    private var mData: ArrayList<HomeItemData> = ArrayList()

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private val contentScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + Job())
    }

    private var name: String by Delegates.observable("") { prop: KProperty<*>, old: String, new: String ->
        Log.e(TAG, "new value is $new")
    }

    private var nameP: String by Delegates.vetoable("default") { prop: KProperty<*>, old: String, new: String ->
        Log.e(TAG, "nameP old value is $nameP")
        Log.e(TAG, "nameP value is $new")
        false
    }

    private var nameZ: String by Delegates.notNull()
    private var number by Delegates.notNull<Int>()
    private var dt: String by DelegateOne()
    private var dtt: String by DelegateTwo()
    private var dttt: String by DelegateThree()

    private val recycler: RecyclerView by findView(R.id.recycler)

    /**
     * launch的block是真正的协程，kotlin编译器会默认的给suspend函数塞一个Continuation对象,而这个Continuation
     * 本质上就是一个回调可以理解为CallBack，如何做到恢复协程的执行呢？其实就是靠这个Continuation对象，当调用resumeWith函数的
     * 时候表示恢复协程
     *
     * Dispatcher调度器也是上下文CoroutineContext因为CoroutineDispatcher间接继承了CoroutineContext，
     * 所有的调度器（Main、Default、Unconfined、IO）都是CoroutineDispatcher的实现，CoroutineDispatcher
     * 也是一个拦截器，因为它实现了ContinuationInterceptor接口因此会重写interceptContinuation函数。
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val intent = Intent(this, AnrDisplayService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }
//        val intent = Intent(this, AnrDisplayActivity::class.java)
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
////                    intent.putExtra("queue", monitorQueue.getQueue().toArray());
//        //                    intent.putExtra("queue", monitorQueue.getQueue().toArray());
//        startActivity(intent)
//        ZRouter.newInstance().setPageName("anr").setAction("jump").setTradeLine("lib")
//            .navigation(this@MainActivity)
//        tradeLine = "lib",action = "jump" ,pageName = "anr"
        val loginModuleService =
            CenterPlugin.getInstance().moduleServiceCenter.getService(ModuleInfoTable.MODULE_LOGIN) as LoginModuleService
        loginModuleService.isLogin
        loginModuleService.attachContext(this)
        //广播
        register()
        //初始化View
        initRecyclerView()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // 设置URI
                val uri_user: Uri = Uri.parse("content://com.lc.provider.myapplication/user")
                // 获取ContentResolver
                val resolver = contentResolver
                // 通过ContentResolver 向ContentProvider中查询数据
                val cursor: Cursor? =
                    resolver.query(uri_user, arrayOf("_id", "name"), null, null, null)
                while (cursor?.moveToNext() == true) {
                    // 将表中数据全部输出
                    println(
                        "query book:" + cursor.getInt(0).toString() + " " + cursor.getString(1)
                    )
                }
                cursor?.close()
            }
        }
        lifecycleScope.launchWhenResumed {
            Log.i("tag", "launchWhenResumed")
        }

        TAG = MainActivity::class.java.simpleName

        //------------委托------------
//        Log.e(TAG,dt)
//        Log.e(TAG,dtt)
//        dt="new value"
//        dtt="new dtt"
//        Log.e(TAG,dt)
//        Log.e(TAG,dtt)
//        Log.e(TAG,dttt)
//        name="new name"
//        nameP="name P"
//        Log.e(TAG,"")
        //----------Flow---------
//        FlowDemo().test()
//        runBlocking {
//            val value = InlineClassDemo(12)
//            InlineClassDemo(12).add()
//            val valueString = InlineClassString("123")
//            valueString.add("123")
//        }
        //-----------Channel------------
//        ChannelDemo().test()
        //-------------------withContext---------------
//        GlobalScope.launch(CoroutineName("1234")) {
////            withContext(Dispatchers.Main) {
//////                async {
//////                    Log.e(TAG, "thread is" + Thread.currentThread())
//////                    doSomeThing2()
//////                }
//////            }
//            /**
//             *
//             */
//            Log.e(TAG, "跑起来了")
//            Log.e(TAG, "thread is" + Thread.currentThread())
////            //注意协程默认是运行在子线程中的，因为默认的调度器是Default
////            launch {
////                Log.e(TAG, "thread is" + Thread.currentThread())
////                Thread.sleep(1000)
////            }
////            launch {
////                Log.e(TAG, "thread is" + Thread.currentThread())
////                Thread.sleep(2000)
////            }
////            launch {
////                Log.e(TAG, "thread is" + Thread.currentThread())
////            }
//            val context = coroutineContext
//            /**
//             * 运行结果：
//             * 2022-12-07 12:29:37.582 26543-26606/com.example.myapplication E/MainActivity: 跑起来了
//             * 2022-12-07 12:29:37.583 26543-26606/com.example.myapplication E/MainActivity: thread isThread[DefaultDispatcher-worker-2,5,main]
//             * 2022-12-07 12:29:37.590 26543-26605/com.example.myapplication E/MainActivity: thread isThread[DefaultDispatcher-worker-1,5,main]
//             * 2022-12-07 12:29:37.591 26543-26608/com.example.myapplication E/MainActivity: thread isThread[DefaultDispatcher-worker-3,5,main]
//             * 2022-12-07 12:29:37.595 26543-26609/com.example.myapplication E/MainActivity: thread isThread[DefaultDispatcher-worker-4,5,main]
//             *
//             *
//             * 结论是默认情况下协程是使用的Default调度器，然后Default的背后应是有一个线程池的支持
//             */
//            Log.e(TAG, "thread is" + Thread.currentThread() + "name:" + context[CoroutineName])
//            withContext(Dispatchers.Main + CoroutineName("12345")) {
//                Log.e(
//                    TAG,
//                    "thread is" + Thread.currentThread() + "name:" + coroutineContext[CoroutineName]
//                )
//            }
//            Log.e(TAG, "thread is" + Thread.currentThread() + "name:" + context[CoroutineName])
//        }

//        //------------------------async/launch----------------------------
//        GlobalScope.launch(Dispatchers.Main + CoroutineName("testCoroutine")) {
////            println(coroutineContext[Job]) // "coroutine#1":StandaloneCoroutine{Active}@1ff62014
//            Log.e(TAG, "thread is" + Thread.currentThread())
////            val job=launch(Dispatchers.IO) {
////                doSomeThing()
////            }
////            job.join()
//            /**
//             * 运行结果：
//             * 2022-12-06 07:19:53.218 23768-23768/com.example.myapplication E/MainActivity: thread isThread[main,5,main]
//             * 2022-12-06 07:19:53.229 23768-23850/com.example.myapplication E/MainActivity: doSomeThing
//             * 2022-12-06 07:19:53.229 23768-23850/com.example.myapplication E/MainActivity: thread isThread[DefaultDispatcher-worker-1,5,main]
//             * 2022-12-06 07:19:53.348 23768-23768/com.example.myapplication E/MainActivity: doSomeThing1
//             * 2022-12-06 07:19:53.348 23768-23768/com.example.myapplication E/MainActivity: doSomeThing1 thread isThread[main,5,main]
//             */
//            val obj = async(Dispatchers.IO) {
//                doSomeThing()
//            }
//            val result = obj.await()
//            Log.e(TAG, result)
//            doSomeThing1()
//            /**
//             * 运行结果：
//             * 2022-12-06 07:19:53.218 23768-23768/com.example.myapplication E/MainActivity: thread isThread[main,5,main]
//             * 2022-12-06 07:19:53.229 23768-23850/com.example.myapplication E/MainActivity: doSomeThing
//             * 2022-12-06 07:19:53.229 23768-23850/com.example.myapplication E/MainActivity: thread isThread[DefaultDispatcher-worker-1,5,main]
//             * 2022-12-06 07:19:53.300 23768-23891/com.example.myapplication E/MainActivity: result
//             * 2022-12-06 07:19:53.348 23768-23768/com.example.myapplication E/MainActivity: doSomeThing1
//             * 2022-12-06 07:19:53.348 23768-23768/com.example.myapplication E/MainActivity: doSomeThing1 thread isThread[main,5,main]
//             */
//
//            /**
//             * 所以一般来说启动一个协程有两个方法launch和async，同样都是异步的启动一个协程，那么它们有什么区别？分别从一下几个角度来阐述
//             * 1 代码的角度
//             * async返回的是一个Deferred对象而launch返回的就是一个Job
//             * 2 使用场景的角度
//             * async可以拿到协程执行的结果launch不行，async很明显适合并发请求同步等待结果的场景，launch使用的场景就是简单的启动一个协程
//             */
//        }

//        contentScope.launch {
////            Log.e(TAG,"run coroutine before delay")
////            delay(1000)
////            Log.e(TAG,"run coroutine after delay")
//            doSomeThing()
//
//        }
    }

    private fun initRecyclerView() {
        val liveData = MutableLiveData<String>();
//        liveData.observe(this
//        ) { t -> Log.e("initRecyclerView", t.toString()) }
//
//        for (i in 0..10){
//            liveData.value=i.toString()
//        }
        mData.add(HomeItemData("PagingActivity", PagingActivity::class.java))
        mData.add(HomeItemData("LiveDataActivity", LiveDataActivity::class.java))
        mData.add(HomeItemData("BookManagerActivity", BookManagerActivity::class.java))
        mData.add(HomeItemData("ViewActivity", ViewActivity::class.java))
        mData.add(HomeItemData("MenuCateActivity", ViewActivity::class.java))

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setRecycledViewPool(object : RecyclerView.RecycledViewPool() {

        })
        val myView = MyView(this)
        val adapter = HomeRecyclerAdapter(mData)
//        recycler.setHasFixedSize(true)
        recycler.adapter = adapter
        adapter.setItemClickListener(object : HomeRecyclerAdapter.OnItemClickListener {
            override fun onItemClickMethod(view: View?, position: Int) {
//                ZRouter.newInstance().setPageName("login").setAction("jump").setTradeLine("ac")
//                    .navigation(this@MainActivity)
//                val newString: String = mData[position].itemName + "1"
//                mData[position].itemName = newString
//                myView.invalidate()
//                adapter.notifyItemChanged(position)
//                adapter.notifyItemInserted(position)
//                adapter.notifyItemMoved()
//                adapter.notifyItemRangeChanged(position)
                if (mData[position].itemName == "MenuCateActivity") {
                    ZRouter.newInstance().setPageName("cateInfo").setAction("jump")
                        .setTradeLine("menu")
                        .navigation(this@MainActivity)
                } else
                    startActivity(Intent(view?.context, mData[position].clazz))
            }
        })
//        adapter.notifyDataSetChanged()
//        recycler.invalidate()
    }

    private fun register() {

    }

    private fun doSomeThing2() {

    }

    /**
     *
     * suspend关键字用来标示函数，一般我说被suspend修饰的都是挂起函数。但是挂起函数并不一定都需要真正的挂起，
     * 例如一个普通的函数如doSomeThing1()编译器会提示Redundant 'suspend' modifier，意思是suspend是
     * 多余的修饰符，但是如果函数中包含了withContext、suspendCoroutine或者await的时候提示就会消失了，这点
     * 很有意思是不是？所以真正的挂起操作还是系统的函数做的。这三个函数的源码都调用了suspendCoroutineUninterceptedOrReturn
     * 这个函数源码是找不到的，只知道这个函数可以帮我们拿到当前正在运行的协程的Continuation对象，一般情况下
     * 在内部调用getResult函数。
     *
     * 具体是如何挂起的？协程启动的时候会执行resumeWith函数这一点毋庸置疑，仔细查看这个函数内部会发现
     *
     */
    private suspend fun doSomeThing(): String {
        withContext(Dispatchers.Main) {
            //        doSomeThing1()
            Log.e(TAG, "doSomeThing")
//        Thread.sleep(5000)
            Log.e(TAG, "thread is" + Thread.currentThread())
        }
//        suspendCoroutine<String> {
//
//        }
//        contentScope.async {  }.join()
        return "result";
    }

    private suspend fun doSomeThing1(): Int {

        Log.e(TAG, "doSomeThing1")
        Log.e(TAG, "doSomeThing1 thread is" + Thread.currentThread())
        return suspendFun() as Int
    }

    private fun suspendFun(): Any {
        return COROUTINE_SUSPENDED
    }

    override fun onDestroy() {
        super.onDestroy()
        unregister()
    }

    private fun unregister() {

    }
}