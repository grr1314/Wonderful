package com.example.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.mylibrary.ModuleInfoTable
import com.example.mylibrary.ModuleServiceCenterProvider
import com.example.mylibrary.callback.ImListener
import com.example.mylibrary.findView
import com.example.mylibrary.service.ImModuleService
import com.lc.routerlib.core.ZRouter
import com.sjk.apt_annotation.Route

@Route(action = "jump", tradeLine = "login", pageName = "login")
class LoginActivity : AppCompatActivity(), ImListener {
    private val editText: EditText by findView(R.id.ed_userId)
    private val editTextTarget: EditText by findView(R.id.ed_target_userId)
    private val loginBtn: Button by findView(R.id.btn_im_login)
    private val logoutBtn: Button by findView(R.id.btn_im_logout)
    private val chatBtn: Button by findView(R.id.btn_im_chat)
    private var imModuleService: ImModuleService? = null

    //007eJxTYEh3O8lQ8DCx7En0Cm2rO5fUO6Idygxa2OuMN5XL7lBK4VFgMEkzt0g1TU4zNEw2M7E0MUiyNEhNNTEzNDM0SkpJMjQ7NiUspSGQkWEn11sGJgZGMATx2RkMjYxNTM3MGaDCKIIALmwfTg==
    //007eJxTYGh6fuLdTPG7+cwmcxpue0gfuXxc6+WxrassLwsuWuQ6T/mRAoNJmrlFqmlymqFhspmJpYlBkqVBaqqJmaGZoVFSSpKh2dcpYSkNgYwMG25PYGFiYARDEJ+dwdDI2MTUzJyBgQkuyAYVBADVKSPr
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        imModuleService = ModuleServiceCenterProvider.getInstance().moduleServiceCenter.getService(
            ModuleInfoTable.MODULE_IM
        ) as ImModuleService
        imModuleService?.addImListener(this)
        loginBtn.setOnClickListener {
            val tokenString =
                if (editText.text.toString() == "123456") "007eJxTYPj+lLNSuVtEQUhhi7Zih+qybRczO+fNUmu+0c3ivefG2fsKDCZp5happslphobJZiaWJgZJlgapqSZmhmaGRkkpSYZmNgWRKQ2BjAwdBc6sjAxMDIxACOKzMRgaGZuYmgEA5Kwdbw=="
                else "007eJxTYDh14fkOJQMJ+19Kc632MWR3XsyQN3u3WGG3VCjXqnUWn9IUGEzSzC1STZPTDA2TzUwsTQySLA1SU03MDM0MjZJSkgzNkgsiUxoCGRnqGt6wMDIwMTACIYjPzmBoZGxiamYOAClGHk0="

            imModuleService?.imLogin(
                tokenString, editText.text.toString()
            )
        }
        logoutBtn.setOnClickListener {
            imModuleService?.imLogout()
        }
        chatBtn.setOnClickListener {
            val target = editTextTarget.text.toString()
            val user = editText.text.toString()
            ZRouter.newInstance().setPageName("chat").setTradeLine("im").setAction("jump")
                .putParams("targetUserID", target).putParams("userID", user).navigation(this)
        }
    }

    override fun unReadCountChange(count: Int) {


    }

    override fun onDestroy() {
        super.onDestroy()
        imModuleService?.removeImListener(this)
    }
}