package com.lc.im

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
import com.lc.im.adapter.SimpleChatAdapter
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
        initView()
        addWatcher()
        ImRtmClient.startTalkWithUser(targetUserID)
    }

    private fun addWatcher() {
        ImRtmClient.addWatcher(userID, this)
        ImRtmClient.addWatcher(targetUserID, this)
    }

    private fun initView() {
        sendBtn.setOnClickListener {
            val content = edSendInfo.text.toString()
            Toast.makeText(applicationContext, content + "", Toast.LENGTH_LONG).show()
            ImRtmClient.sendMessage(userID ?: "", content, targetUserID.toString())
        }
        ivBack.setOnClickListener {
            finish()
        }
        tvTitle.text = targetUserID

        chatView.layoutManager = LinearLayoutManager(this)
        chatView.adapter =simpleAdapter
    }

    /**
     * 说明有消息接收到了，不管是哪个源
     */
    @SuppressLint("LongLogTag")
    override fun onMessage(imInfo: ImInfo?) {
        simpleAdapter.add(imInfo)
        Log.i("RtmMessageCentralStation", "收到消息内容：" + imInfo?.target?.text)
    }

    override fun unReadCount(count: Int) {
        if (count <= 0) {
            tvUnRead.text = ""
        } else {
            tvUnRead.text = count.toString()
        }
    }

    override fun onStop() {
        super.onStop()
        ImRtmClient.stopTalkWithUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        ImRtmClient.removeWatcher(targetUserID)
        ImRtmClient.removeWatcher(userID)
    }
}