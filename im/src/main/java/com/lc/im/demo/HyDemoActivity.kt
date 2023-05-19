package com.lc.im.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.mylibrary.BaseActivity
import com.example.mylibrary.findView
import com.example.mylibrary.loadUrl
import com.hyphenate.EMCallBack
import com.hyphenate.EMContactListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMUserInfo
import com.lc.im.GlideEngine
import com.lc.im.OssManager
import com.lc.im.R
import com.lc.im.Source
import com.lc.im.UserInfoManager
import com.lc.im.hyphenate.HyImClient
import com.lc.im.hyphenate.HyUserInfoManager
import com.lc.im.hyphenate.HyphenateMessageCentralStation
import com.lc.im.hyphenate.listener.HyContactsListener
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.sjk.apt_annotation.Route
import com.sjk.apt_annotation.RouteParam
import com.tencent.mmkv.MMKV
import java.io.File
import java.util.ArrayList

/**
 * created by lvchao 2023/5/11
 * describe:
 */
@RouteParam
@Route(action = "jump", tradeLine = "im", pageName = "hy_demo")
public class HyDemoActivity : BaseActivity(), EMContactListener, EMMessageListener {
    private var mImageList: ArrayList<File>? = null
    private val registerBtn: Button by findView(R.id.btn_register)
    private val edRegisterAccount: EditText by findView(R.id.tv_register_account)
    private val edRegisterPwd: EditText by findView(R.id.tv_register_pwd)

    private val loginBtn: Button by findView(R.id.btn_login)
    private val logoutBtn: Button by findView(R.id.btn_logout)

    private val edLoginAccount: EditText by findView(R.id.tv_login_account)
    private val edLoginPwd: EditText by findView(R.id.tv_login_pwd)

    private val addBtn: Button by findView(R.id.btn_add)
    private val edFriend: EditText by findView(R.id.ed_friend_name)
    private val tvFriendList: TextView by findView(R.id.tv_friend_list)

    private val acceptBtn: Button by findView(R.id.btn_accept)
    private val rejectBtn: Button by findView(R.id.btn_reject)
    private val tvWho: TextView by findView(R.id.tv_friend_who)

    private val sendBtn: Button by findView(R.id.btn_send)
    private val edToUser: EditText by findView(R.id.ed_to_user)
    private val edContent: EditText by findView(R.id.ed_msg_content)
    private val conversationBtn: Button by findView(R.id.btn_conversation_list)

    private val ivUserIcon: ImageView by findView(R.id.iv_user_icon)
    private val changeBtn: Button by findView(R.id.btn_change)
    private val edNickName: EditText by findView(R.id.ed_nickname)
    private val edSign: EditText by findView(R.id.ed_sign)

    private var username: String? = null
    var kv = MMKV.defaultMMKV()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hy_demo)
        HyImClient.addEMContactListener(this)
        HyImClient.addMsgReceivedListener(this)
        register()
        login()
        addFriend()
        who()
        send()
        friendList()
        user()
    }

    private fun user() {
        HyImClient.hyUserInfoManager?.getSingleUserInfo(
            EMClient.getInstance().currentUser,
            object : UserInfoManager.UserInfoListener<EMUserInfo> {
                override fun getAllUserInfo(uinfos: MutableList<EMUserInfo>?) {
                }

                override fun getSingleUserInfo(emUserInfo: EMUserInfo?) {
                    ivUserIcon.loadUrl(this@HyDemoActivity, emUserInfo?.avatarUrl.toString())
                }

                override fun setSingleInfoSuccess(type: String?) {
                }

                override fun setUserInfoSuccess(success: String?) {
                }

                override fun onError(error: String?) {
                }

            },
            false
        )
        mImageList = ArrayList()
        ivUserIcon.setOnClickListener {
            PictureSelector.create(this).openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine()) // 这里就是设置图片加载引擎
                .setMaxSelectNum(1).setSelectedData(null) // 设置已被选中的数据
                .forResult(PictureConfig.CHOOSE_REQUEST);
        }

        changeBtn.setOnClickListener {
            HyImClient.hyUserInfoManager?.setSingleInfo(EMUserInfo.EMUserInfoType.NICKNAME,
                edNickName.text.toString(),
                object : UserInfoManager.UserInfoListener<EMUserInfo> {
                    override fun getAllUserInfo(uinfos: MutableList<EMUserInfo>?) {
                    }

                    override fun getSingleUserInfo(t: EMUserInfo?) {
                        Toast.makeText(applicationContext, "设置成功", Toast.LENGTH_LONG).show()
                    }

                    override fun setSingleInfoSuccess(type: String?) {

                    }

                    override fun setUserInfoSuccess(success: String?) {
                    }

                    override fun onError(error: String?) {
                    }
                })
            if (mImageList!!.size > 0) {
                val file = mImageList!![0]
                var url = ""
                if (file != null) {
                    url = file.absolutePath;
                }
            }
//            val ossManager = OssManager()
//            ossManager.init(this)
//            ossManager.upload(url, "11")
            val iconUrl =
                "https://pic1.zhimg.com/7dee082d74f641b7b20f8a203a4d5de4_1440w.jpg?source=172ae18b"
            HyImClient.hyUserInfoManager?.setUserIcon(iconUrl,
                object : UserInfoManager.UserInfoListener<EMUserInfo> {
                    override fun getAllUserInfo(uinfos: MutableList<EMUserInfo>?) {
                    }

                    override fun getSingleUserInfo(t: EMUserInfo?) {
                    }

                    override fun setSingleInfoSuccess(type: String?) {
                        Toast.makeText(applicationContext, "设置头像成功", Toast.LENGTH_LONG).show()
                    }

                    override fun setUserInfoSuccess(success: String?) {
                    }

                    override fun onError(error: String?) {
                        Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
                    }

                })
        }
    }

    private fun send() {
        sendBtn.setOnClickListener {
            HyImClient.sendMessage("",
                edContent.text.toString(),
                edToUser.text.toString(),
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
        }
        conversationBtn.setOnClickListener {
            val intent = Intent(this, HyConversationListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun friendList() {
        HyImClient.getAllContact(object : HyContactsListener {
            override fun getAllContractsSuccess(contracts: MutableList<String>?) {
                if (contracts != null) {
                    var listString = "好友列表:"
                    for (username: String in contracts) {
                        listString = listString + "\n" + username
                    }
                    tvFriendList.text = listString
                }
            }

            override fun getAllContractsFail(code: Int, reason: String?) {
            }

        })
    }

    private fun who() {
        acceptBtn.setOnClickListener {
            HyImClient.replyContact(true, username.toString())
        }

        rejectBtn.setOnClickListener {
            HyImClient.replyContact(false, username.toString())
        }
    }

    private fun addFriend() {
        addBtn.setOnClickListener {
            HyImClient.addContact(edFriend.text.toString(), "12")
        }
    }

    private fun login() {
        logoutBtn.setOnClickListener {
            HyImClient.logout(object : EMCallBack {
                override fun onSuccess() {
                    Log.w(
                        "HyDemoActivity", "退出成功"
                    )
                }

                override fun onError(errorCode: Int, errorInfo: String?) {
                    Log.w(
                        "HyDemoActivity", "退出失败！原因是：$errorInfo code 是$errorCode"
                    )
                }

            })
        }
        loginBtn.setOnClickListener {
            HyImClient.login(edLoginAccount.text.toString(),
                edLoginPwd.text.toString(),
                object : EMCallBack {
                    override fun onSuccess() {
                        kv.encode("userId", edLoginAccount.text.toString())
                        val hyphenateMessageCentralStation: HyphenateMessageCentralStation? =
                            HyImClient.hyphenateMessageCentralStation
                        hyphenateMessageCentralStation?.start()
                        hyphenateMessageCentralStation?.setSelfUserId(edLoginAccount.text.toString())
                        //获取当前用户信息
                        HyImClient.hyUserInfoManager?.getSingleUserInfo(
                            edLoginAccount.text.toString(),
                            object : UserInfoManager.UserInfoListener<EMUserInfo> {
                                override fun getAllUserInfo(uinfos: MutableList<EMUserInfo>?) {

                                }

                                override fun getSingleUserInfo(t: EMUserInfo?) {
                                    edNickName.setText(t?.nickname)
                                    edSign.setText(t?.signature)
                                }

                                override fun setSingleInfoSuccess(type: String?) {
                                }

                                override fun setUserInfoSuccess(success: String?) {
                                }

                                override fun onError(error: String?) {
                                }

                            },
                            false
                        )
                        Log.w(
                            "HyDemoActivity", "登录成功"
                        )
                    }

                    override fun onError(errorCode: Int, errorInfo: String?) {
                        Log.w(
                            "HyDemoActivity", "登录失败！原因是：$errorInfo code 是$errorCode"
                        )
                    }
                })
        }
    }

    private fun register() {
        registerBtn.setOnClickListener {
            HyImClient.registerAccount(
                edRegisterAccount.text.toString(), edRegisterPwd.text.toString()
            )
        }
    }

    // 联系人已添加。
    override fun onContactAdded(username: String?) {
        Log.w(
            "HyDemoActivity", "onContactAdded"
        )
    }

    // 联系人被删除。
    override fun onContactDeleted(username: String?) {
        Log.w(
            "HyDemoActivity", "onContactDeleted"
        )
    }

    // 接收到好友请求。
    @SuppressLint("SetTextI18n")
    override fun onContactInvited(username: String?, reason: String?) {
        Log.w(
            "HyDemoActivity", "接收到好友请求"
        )
        tvWho.text = username + "发起好友申请，具体理由是：" + reason
    }

    // 对方同意了好友请求。
    override fun onFriendRequestAccepted(username: String?) {
        Log.w(
            "HyDemoActivity", "onFriendRequestAccepted"
        )
    }

    // 对方拒绝了好友请求。
    override fun onFriendRequestDeclined(username: String?) {
        Log.w(
            "HyDemoActivity", "onFriendRequestDeclined"
        )
    }

    override fun onMessageReceived(message: MutableList<EMMessage>?) {
        message?.let {
            for (msg in it) {
                HyImClient.hyphenateMessageCentralStation?.addFromNet(
                    msg, Source.NET, msg.conversationId()
                )
                Log.w(
                    "HyDemoActivity",
                    "消息接收成功" + "\n" + "from:" + msg.from + "\n" + "内容：" + msg.body?.describeContents()
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
                val result = PictureSelector.obtainSelectorList(data)
                analyticalSelectResults(result)
            }
        }
    }

    private fun analyticalSelectResults(result: ArrayList<LocalMedia>?) {
        if (result != null) {
            mImageList?.clear()
            for (media in result) {
                //            if (media.getWidth() == 0 || media.getHeight() == 0) {
                //                if (PictureMimeType.isHasImage(media.getMimeType())) {
                //                    MediaExtraInfo imageExtraInfo = MediaUtils.getImageSize(this, media.getPath());
                //                    media.setWidth(imageExtraInfo.getWidth());
                //                    media.setHeight(imageExtraInfo.getHeight());
                //                } else if (PictureMimeType.isHasVideo(media.getMimeType())) {
                //                    MediaExtraInfo videoExtraInfo = MediaUtils.getVideoSize(this, media.getPath());
                //                    media.setWidth(videoExtraInfo.getWidth());
                //                    media.setHeight(videoExtraInfo.getHeight());
                //                }
                //            }
                //            Log.i(TAG, "文件名: " + media.getFileName());
                //            Log.i(TAG, "是否压缩:" + media.isCompressed());
                //            Log.i(TAG, "压缩:" + media.getCompressPath());
                //            Log.i(TAG, "初始路径:" + media.getPath());
                //            Log.i(TAG, "绝对路径:" + media.getRealPath());
                //            Log.i(TAG, "是否裁剪:" + media.isCut());
                //            Log.i(TAG, "裁剪:" + media.getCutPath());
                //            Log.i(TAG, "是否开启原图:" + media.isOriginal());
                //            Log.i(TAG, "原图路径:" + media.getOriginalPath());
                //            Log.i(TAG, "沙盒路径:" + media.getSandboxPath());
                //            Log.i(TAG, "水印路径:" + media.getWatermarkPath());
                //            Log.i(TAG, "视频缩略图:" + media.getVideoThumbnailPath());
                //            Log.i(TAG, "原始宽高: " + media.getWidth() + "x" + media.getHeight());
                //            Log.i(TAG, "裁剪宽高: " + media.getCropImageWidth() + "x" + media.getCropImageHeight());
                //            Log.i(TAG, "文件大小: " + media.getSize());
                mImageList?.add(File(media.realPath)); // 接收已选图片地址，用于接口上传图片
            }
            loadImageComplete()
        }
    }

    private fun loadImageComplete() {
        Glide.with(this).load(mImageList?.get(0)).into(ivUserIcon)
    }

//    /**
//     * 图片选择器回调
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == PictureConfig.CHOOSE_REQUEST || requestCode == PictureConfig.REQUEST_CAMERA) {
//                ArrayList<LocalMedia> result = PictureSelector.obtainSelectorList(data);
//                analyticalSelectResults(result);
//            }
//        } else if (resultCode == RESULT_CANCELED) {
//            Log.i("TAG", "onActivityResult PictureSelector Cancel");
//        }
//    }
}