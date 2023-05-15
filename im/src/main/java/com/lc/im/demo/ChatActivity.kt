package com.lc.im.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mylibrary.BaseActivity
import com.example.mylibrary.findView
import com.hyphenate.EMCallBack
import com.lc.im.MsgWatcher
import com.lc.im.R
import com.lc.im.demo.adapter.SimpleChatAdapter
import com.lc.im.hyphenate.HyImClient
import com.lc.im.model.ImInfo
import com.sjk.apt_annotation.Route
import com.sjk.apt_annotation.RouteParam

/**
 * created by lvchao 2023/5/6
 * describe:
 */
@RouteParam
@Route(action = "jump", tradeLine = "im", pageName = "chat")
class ChatActivity : BaseActivity(), MsgWatcher {
    @RouteParam(name = "targetUserID")
    var targetUserID: String? = null

    @RouteParam(name = "userID")
    var userID: String? = null

    @RouteParam(name = "nickname")
    var nickName: String? = null

    private val sendBtn: Button by findView(R.id.btn_send)
    private val edSendInfo: AppCompatEditText by findView(R.id.ed_send_info)
    private val chatView: RecyclerView by findView(R.id.chat_view)

    private val ivBack: ImageView by findView(R.id.iv_back)
    private val tvUnRead: TextView by findView(R.id.tv_right_des)
    private val tvTitle: TextView by findView(R.id.tv_center_des)
    private val content: List<ImInfo> = ArrayList()
    private var simpleAdapter = SimpleChatAdapter(content)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        addWatcher()
        HyImClient.startTalkWithUser(targetUserID)
        initView()
    }

    private fun addWatcher() {
        HyImClient.addWatcher(userID, this)
        HyImClient.addWatcher(targetUserID, this)
//        AgoraImClient.addWatcher(userID, this)
//        AgoraImClient.addWatcher(targetUserID, this)
    }

    private fun initView() {
        tvTitle.text = nickName
        sendBtn.setOnClickListener {
            val content = edSendInfo.text.toString()
            Toast.makeText(applicationContext, content + "", Toast.LENGTH_LONG).show()
            HyImClient.sendMessage(userID ?: "",
                content,
                targetUserID.toString(),
                object : EMCallBack {
                    override fun onSuccess() {
                        Log.w("HyImClient", "消息发送成功")
                    }

                    override fun onError(code: Int, info: String?) {
                        Log.w("HyImClient", "发送失败！原因：" + info + "code:" + code)
                    }

                    override fun onProgress(p0: Int, p1: String?) {
                        super.onProgress(p0, p1)
                        Log.w("HyImClient", "onProgress:$p1 code:$p0")
                    }

                })
//            AgoraImClient.sendMessage(userID ?: "", content, targetUserID.toString())
        }
        ivBack.setOnClickListener {
            finish()
        }
        tvTitle.text = targetUserID
        chatView.layoutManager = LinearLayoutManager(this)
        chatView.adapter = simpleAdapter
//        HyImClient.hyphenateMessageCentralStation?.loadHistoryMessage()
    }

    /**
     * 说明有消息接收到了，不管是哪个源
     */
    @SuppressLint("LongLogTag")
    override fun onMessage(imInfo: ImInfo?) {
        simpleAdapter.add(imInfo, false)
        Log.i("RtmMessageCentralStation", "收到消息内容：" + imInfo?.target?.text)
    }

    override fun unReadCount(count: Int) {
        if (count <= 0) {
            tvUnRead.text = ""
        } else {
            tvUnRead.text = count.toString()
        }
    }

    @SuppressLint("LongLogTag")
    override fun startLoadHistoryMessage() {
        Log.i("RtmMessageCentralStation", "开始加载历史消息记录")
    }

    override fun loadHistoryMessageSuccess(imInfoList: MutableList<ImInfo>?) {
        if (imInfoList != null) {
            for (imInfo in imInfoList) {
                simpleAdapter.add(imInfo, true)
            }
        }
    }

    @SuppressLint("LongLogTag")
    override fun loadHistoryMessageFail() {
        //没有数据了，不用加载更多了
        Log.i("RtmMessageCentralStation", "没有数据了，不用加载更多了")
    }

    @SuppressLint("LongLogTag")
    override fun loadHistoryMessageComplete() {
        Log.i("RtmMessageCentralStation", "加载完成了，是时候关闭加载ui了")
    }

    override fun onStop() {
        super.onStop()
        HyImClient.stopTalkWithUser()
//        AgoraImClient.stopTalkWithUser()
    }

    override fun onDestroy() {
        super.onDestroy()
//        AgoraImClient.removeWatcher(targetUserID)
//        AgoraImClient.removeWatcher(userID)
        HyImClient.removeWatcher(targetUserID)
        HyImClient.removeWatcher(userID)
    }
}