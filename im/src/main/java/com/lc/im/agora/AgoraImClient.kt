package com.lc.im.agora

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.mylibrary.callback.ImListener
import com.lc.im.ImClient
import com.lc.im.MsgWatcher
import com.lc.im.db.ImCache
import com.tencent.mmkv.MMKV
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmChannelAttribute
import io.agora.rtm.RtmChannelListener
import io.agora.rtm.RtmChannelMember
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmMessage
import io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED
import io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_CONNECTED
import io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_CONNECTING
import io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_DISCONNECTED
import io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING
import io.agora.rtm.SendMessageOptions

/**
 * created by lvchao 2023/5/5
 * describe: 它全局单例模式
 *
 * 注意！！！！！声网的RTM即时通讯（云信令）不适合做IM聊天软件，因为它不支持消息推送！！！！日了🐶
 * 想了一下可能比较适合的场景是（1）直播中的聊天（2）视频会议的聊天
 */
@SuppressLint("StaticFieldLeak")
object AgoraImClient : ImClient<AgoraImClient>{
    private var rtmClient: RtmClient? = null
    private var page = 1

    private const val APP_ID = "4f78e5cf11c64940b90ee461612bdb16"

    var currentState = CONNECTION_STATE_DISCONNECTED
    private var mContext: Context? = null
    var agoraMessageCentralStation: AgoraMessageCentralStation? = null

    override fun with(context: Context?): AgoraImClient {
        Log.w("ImRtmClient", "with")
        if (mContext == null && context == null) throw Exception("context can not be null")
        mContext = context
        if (agoraMessageCentralStation == null) agoraMessageCentralStation =
            AgoraMessageCentralStation(ImCache(context))
        return this
    }


    private fun getRtmClient() {
        if (rtmClient == null) {
            rtmClient = RtmClient.createInstance(mContext!!, APP_ID, object : RtmClientListener {
                override fun onConnectionStateChanged(state: Int, reason: Int) {
                    currentState = state
                    Log.w("ImRtmClient", "State changed! now current state is $state")
                    //state是状态，reason是触发原因 具体请看https://docs.agora.io/cn/Real-time-Messaging/API%20Reference/RTM_java/interfaceio_1_1agora_1_1rtm_1_1_rtm_status_code_1_1_connection_state.html#a3d82ccb518480a5c1eb6008a0ca86574
                    when (state) {
                        CONNECTION_STATE_DISCONNECTED -> {
                            //初始状态，或者是logout成功之后的状态
                        }

                        CONNECTION_STATE_CONNECTING -> {
                            //正在连接的状态
                        }

                        CONNECTION_STATE_CONNECTED -> {
                            //连接成功，login成功之后会进入该状态
                        }

                        CONNECTION_STATE_RECONNECTING -> {
                            //网络原因出现连接断开之后进入这个状态
                        }

                        CONNECTION_STATE_ABORTED -> {
                            //单点登录互踢的情况
                        }
                    }
                }


                override fun onMessageReceived(rtmMessage: RtmMessage?, peerId: String?) {
                    //收到点对点的消息，rtmMessage是消息主体，peerId是对方的用户Id
                    agoraMessageCentralStation?.addFromNet(rtmMessage, 0, peerId)
                    Log.w(
                        "ImRtmClient",
                        "onMessageReceived has been called the peerId is" + peerId + "and the message content as follows：" + "text-> " + rtmMessage?.text + "messageType->" + rtmMessage?.messageType
                    )
                }

                /**
                 * 当前使用的 RTM Token 已超过签发有效期。
                 * 如果 Token 过期时，用户处于 CONNECTION_STATE_CONNECTED 状态，会收到该回调并切换至 CONNECTION_STATE_ABORTED 状态。此时，用户需要调用 login 方法重新登录。
                 * 如果 Token 过期时，用户由于网络问题处于 CONNECTION_STATE_RECONNECTING 状态，会在网络恢复时收到该回调。此时，用户需要调用 renewToken 方法恢复连接。
                 */
                override fun onTokenExpired() {
                    Log.w(
                        "ImRtmClient", "onTokenExpired"
                    )
                }

                override fun onTokenPrivilegeWillExpire() {
                }

                override fun onPeersOnlineStatusChanged(p0: MutableMap<String, Int>?) {
                    //好友在线状态 ONLINE 在线 UNREACHABLE 不稳定 OFFLINE不在线
                }
            })
        }

    }

    /**
     * 发送消息
     * 注意：目前发送的不是离线消息，如果目标用户不在线的话会报错，所以后面还是要研究如何发送离线消息
     */
    public fun sendMessage(userId: String, message: String, peerId: String) {
        getRtmClient()
        Log.w(
            "ImRtmClient", "userId是：" + userId + "peerId是：" + userId + "msg 是" + message
        )
        val rtm = rtmClient?.createMessage(message)
        agoraMessageCentralStation?.addFromLocal(rtm, 1, peerId)
        rtmClient?.sendMessageToPeer(
            peerId,
            rtm,
            SendMessageOptions(),
            object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    Log.w(
                        "ImRtmClient", "消息发送成功"
                    )
                }

                override fun onFailure(errorInfo: ErrorInfo?) {
                    Log.w(
                        "ImRtmClient",
                        "消息发送失败！原因是：" + errorInfo?.errorDescription + " code 是" + errorInfo?.errorCode
                    )
                }
            })
    }

    public fun createChannel() {
        getRtmClient()
        rtmClient?.createChannel("", object : RtmChannelListener {
            override fun onMemberCountUpdated(count: Int) {

            }

            override fun onAttributesUpdated(mutableList: MutableList<RtmChannelAttribute>?) {
            }

            override fun onMessageReceived(rtmMessage: RtmMessage?, from: RtmChannelMember?) {
            }

            override fun onMemberJoined(rtmChannelMember: RtmChannelMember?) {
            }

            override fun onMemberLeft(rtmChannelMember: RtmChannelMember?) {
            }

        })
    }

    public fun login(token: String, userId: String) {
        login(token, userId, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                Log.w(
                    "ImRtmClient", "登录成功"
                )
                agoraMessageCentralStation?.start()
                agoraMessageCentralStation?.setSelfUserId(userId);
            }

            override fun onFailure(errorInfo: ErrorInfo?) {
                Log.w(
                    "ImRtmClient",
                    "登录失败！原因是：" + errorInfo?.errorDescription + " code 是" + errorInfo?.errorCode
                )
            }

        })
    }

    public fun logout() {
        logout(object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                agoraMessageCentralStation?.stop()
                agoraMessageCentralStation?.setSelfUserId(null);
            }

            override fun onFailure(errorInfo: ErrorInfo?) {

            }
        })
    }

    fun startTalkWithUser(peerId: String?) {
        agoraMessageCentralStation?.setPeerId(peerId)
        page = 1;
        //开始查询历史消息
        loadHistoryMessage()
    }

    fun stopTalkWithUser() {
        agoraMessageCentralStation?.setPeerId(null)
        page = 1;
    }

    private fun loadHistoryMessage() {
        page++
        agoraMessageCentralStation?.loadHistoryMessage(page)
    }

    override fun addWatcher(peerId: String?, msgWatcher: MsgWatcher?) {
        agoraMessageCentralStation?.addWatcher(peerId, msgWatcher)
    }

    override fun removeWatcher(peerId: String?) {
        agoraMessageCentralStation?.removeWatcher(peerId)
    }

    override fun addListener(imListener: ImListener) {
        agoraMessageCentralStation?.addListener(imListener)
    }

    override fun removeListener(imListener: ImListener) {
        agoraMessageCentralStation?.removeListener(imListener)
    }


    public fun logout(resultCallback: ResultCallback<Void>) {
        getRtmClient()
        rtmClient?.logout(resultCallback)
    }

    public fun login(
        token: String, userId: String, callback: ResultCallback<Void>
    ) {
        getRtmClient()
        rtmClient?.login(token, userId, callback)
    }

    public fun start() {
        //判断是否已经登录，如果是则start否则不会start
        val kv = MMKV.defaultMMKV()
        agoraMessageCentralStation?.start()
        Log.i("userId", kv.decodeString("userId"))
        agoraMessageCentralStation?.setSelfUserId(kv.decodeString("userId"))
    }

    public fun stop() {
        agoraMessageCentralStation?.stop()
    }

    public fun clear() {
        mContext = null
    }

}