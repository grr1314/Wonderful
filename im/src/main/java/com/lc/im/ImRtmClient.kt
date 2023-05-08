package com.lc.im

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.mylibrary.callback.ImListener
import com.lc.im.db.ImCache
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
 */
@SuppressLint("StaticFieldLeak")
object ImRtmClient {
    private var rtmClient: RtmClient? = null

    private const val APP_ID = "4f78e5cf11c64940b90ee461612bdb16"

    var currentState = CONNECTION_STATE_DISCONNECTED
    private var mContext: Context? = null
    var rtmMessageCentralStation: RtmMessageCentralStation? = null

    fun with(context: Context?): ImRtmClient {
        Log.w("ImRtmClient", "with")
        if (mContext == null && context == null) throw Exception("context can not be null")
        this.mContext = context
        if (rtmMessageCentralStation == null) rtmMessageCentralStation =
            RtmMessageCentralStation(ImCache(context))
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
                    rtmMessageCentralStation?.addFromNet(rtmMessage, 0, peerId)
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
        rtmMessageCentralStation?.addFromLocal(rtm, 1, peerId)
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
                rtmMessageCentralStation?.start()
                rtmMessageCentralStation?.setSelfUserId(userId);
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
                rtmMessageCentralStation?.stop()
                rtmMessageCentralStation?.setSelfUserId(null);
            }

            override fun onFailure(errorInfo: ErrorInfo?) {

            }
        })
    }

    fun startTalkWithUser(peerId: String?) {
        rtmMessageCentralStation?.setPeerId(peerId)
    }

    fun stopTalkWithUser() {
        rtmMessageCentralStation?.setPeerId(null)
    }

    fun addWatcher(peerId: String?, msgWatcher: MsgWatcher?) {
        rtmMessageCentralStation?.addWatcher(peerId, msgWatcher)
    }

    fun removeWatcher(peerId: String?) {
        rtmMessageCentralStation?.removeWatcher(peerId)
    }

    fun addListener(imListener: ImListener) {
        rtmMessageCentralStation?.addListener(imListener)
    }

    fun removeListener(imListener: ImListener) {
        rtmMessageCentralStation?.removeListener(imListener)
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

}