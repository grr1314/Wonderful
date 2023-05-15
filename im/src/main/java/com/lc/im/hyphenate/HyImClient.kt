package com.lc.im.hyphenate

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.mylibrary.callback.ImListener
import com.hyphenate.EMCallBack
import com.hyphenate.EMContactListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMOptions
import com.hyphenate.exceptions.HyphenateException
import com.lc.im.ImClient
import com.lc.im.MsgWatcher
import com.lc.im.Source
import com.lc.im.agora.AgoraImClient
import com.lc.im.db.ImCache
import com.lc.im.hyphenate.listener.HyContactsListener
import com.tencent.mmkv.MMKV
import java.util.concurrent.ExecutorService


/**
 * created by lvchao 2023/5/10
 * describe:
 */
@SuppressLint("StaticFieldLeak")
object HyImClient : ImClient<HyImClient> {
    private var created = false
    private var mContext: Context? = null
    var hyConversationManager: HyConversationManager? = null
    var hyphenateMessageCentralStation: HyphenateMessageCentralStation? = null
    var hyContactManager: HyContactManager? = null
    var hyUserInfoManager: HyUserInfoManager? = null
    var userInfoCache: HyUserInfoCache? = null
    var executorService: ExecutorService? = null
        set(value) {
            field = value
            hyContactManager?.setExecutorService(value)
            hyConversationManager?.setExecutorService(value)
            hyUserInfoManager?.setExecutorService(value)
        }

    override fun with(context: Context?): HyImClient {
        Log.w("HyImClient", "with")
        if (created.not()) {
            if (mContext == null && context == null) throw Exception("context can not be null")
            mContext = context
            val handler = HyHandler(Looper.getMainLooper())
            //初始化环信SDK
            init()
            userInfoCache = HyUserInfoCache()
            if (hyUserInfoManager == null) {
                hyUserInfoManager = HyUserInfoManager(handler, userInfoCache)
            }
            if (hyContactManager == null) {
                hyConversationManager =
                    HyConversationManager(hyUserInfoManager, handler, userInfoCache)
            }
            if (hyphenateMessageCentralStation == null) {
                hyphenateMessageCentralStation = HyphenateMessageCentralStation(
                    ImCache(context), handler, hyConversationManager, hyUserInfoManager
                )
            }
            if (hyContactManager == null) {
                hyContactManager = HyContactManager(hyUserInfoManager, userInfoCache)
            }
            hyContactManager?.setHandler(handler)
            created = true
        }
        return this
    }

    private fun init() {
        Log.w("HyImClient", "init")
        Log.w(
            "HyImClient",
            "init thread:" + Thread.currentThread() + "appkey:1152230510163667#wonderfulp"
        )
        val option = EMOptions()
        //1152230510163667#wonderful
        option.appKey = "1152230510163667#wonderfulp"
        EMClient.getInstance().init(mContext, option)
    }

    public fun sendMessage(
        userId: String, message: String, peerId: String, emCallBack: EMCallBack
    ) {
        // 创建一条文本消息，`content` 为消息文字内容，`conversationId` 为会话 ID，在单聊时为对端用户 ID、群聊时为群组 ID，聊天室时为聊天室 ID。
        val emMessage = EMMessage.createTextSendMessage(message, peerId)
        hyphenateMessageCentralStation?.addFromLocal(emMessage, Source.LOCAL, peerId)
        emMessage.setMessageStatusCallback(emCallBack)
        // 发送消息。
        EMClient.getInstance().chatManager().sendMessage(emMessage)
    }

    fun stopTalkWithUser() {
        hyphenateMessageCentralStation?.setPeerId(null)
    }

    fun startTalkWithUser(peerId: String?) {
        hyphenateMessageCentralStation?.setPeerId(peerId)
        //开始查询历史消息
        hyphenateMessageCentralStation?.loadHistoryMessage()
    }

    public fun getAllConversations(): MutableMap<String, EMConversation>? {
        return hyConversationManager?.allConversations
    }

    public fun replyContact(accept: Boolean, userName: String) {
        hyContactManager?.replyContact(accept, userName)
    }

    public fun addMsgReceivedListener(msgListener: EMMessageListener) {
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }

    public fun addEMContactListener(em: EMContactListener) {
        hyContactManager?.addEMContactListener(em)
    }

    public fun addHyContactsListener(listener: HyContactsListener) {
        hyContactManager?.registerListener(listener)
    }

    public fun addContact(toAddUsername: String, reason: String) {
        hyContactManager?.addContact(toAddUsername, reason)
    }

    public fun getAllContact(param: HyContactsListener) {
        hyContactManager?.registerListener(param)
        hyContactManager?.getAllContacts(true)
    }

    public fun logout(resultCallback: EMCallBack) {
        EMClient.getInstance().logout(true, resultCallback)
    }

    public fun logout() {
        logout(object : EMCallBack {
            override fun onSuccess() {

            }

            override fun onError(code: Int, message: String) {

            }
        })
    }

    public fun login(account: String, pwd: String) {
        login(account, pwd, object : EMCallBack {
            override fun onSuccess() {
                Log.w(
                    "HyImClient", "登录成功"
                )
            }

            override fun onError(errorCode: Int, errorInfo: String?) {
                Log.w(
                    "HyImClient", "登录失败！原因是：$errorInfo code 是$errorCode"
                )
            }
        })
    }

    public fun login(
        account: String, pwd: String, callback: EMCallBack
    ) {
        EMClient.getInstance().login(account, pwd, callback)
    }

    /**
     * 测试使用
     */
    public fun registerAccount(account: String, pwd: String) {
        Thread(Runnable {
            try {
                EMClient.getInstance().createAccount(account, pwd)
            } catch (e: HyphenateException) {
                Log.w(
                    "HyImClient", "注册失败！原因是：${e.description} code 是${e.errorCode}"
                )
            }
        }).start()
    }

    override fun addWatcher(peerId: String?, msgWatcher: MsgWatcher?) {
        hyphenateMessageCentralStation?.addWatcher(peerId, msgWatcher)
    }

    override fun removeWatcher(peerId: String?) {
        hyphenateMessageCentralStation?.removeWatcher(peerId)
    }

    override fun addListener(imListener: ImListener) {
        hyphenateMessageCentralStation?.addListener(imListener)
    }

    override fun removeListener(imListener: ImListener) {
        hyphenateMessageCentralStation?.removeListener(imListener)
    }

    public class HyHandler(mainLooper: Looper?) : Handler(
        mainLooper!!
    )

    public fun start() {
        //判断是否已经登录，如果是则start否则不会start
        val kv = MMKV.defaultMMKV()
        hyphenateMessageCentralStation?.start()
        hyphenateMessageCentralStation?.setSelfUserId(kv.decodeString("userId"))
    }

    public fun stop() {
        hyphenateMessageCentralStation?.stop()
    }

}