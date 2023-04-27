package com.example.login

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.lc.routerlib.core.ZRouter
import com.sjk.apt_annotation.Route

@Route(action = "jump", tradeLine = "ac", pageName = "login")
class LoginActivity : AppCompatActivity() {
    var btn: TextView? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn= findViewById(R.id.btn)
        btn?.setOnClickListener {
            ZRouter.newInstance().setPageName("page").setAction("jump").setTradeLine("ac")
                .navigation(this@LoginActivity)
        }
    }
}