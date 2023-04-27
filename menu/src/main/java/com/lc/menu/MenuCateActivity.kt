package com.lc.menu

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.mylibrary.findView
import com.lc.menu.viewmodel.MenuViewModel
import com.lc.repository.model.MenuCateInfo
import com.sjk.apt_annotation.Route

@Route(action = "jump", tradeLine = "menu", pageName = "cateInfo")
class MenuCateActivity : AppCompatActivity() {
    lateinit var viewModel: MenuViewModel
    var cateInfoAdapter: CateInfoAdapter? = null
    private val loadingView: SwipeRefreshLayout by findView(R.id.loading)
    private val recyclerView: RecyclerView by findView(R.id.cate_recycler)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cateinfo_activity)
        initViewModel()
        initRecyclerView()
        loadingView.isRefreshing = true
        loadingView.setOnRefreshListener {
            viewModel.getCateInfo(true)
        }
        viewModel.getCateInfo(false)
        viewModel.cateInfoListLiveData.observe(
            this
        ) { t ->
            updateView(t)
            loadingView.isRefreshing = false
        }

        viewModel.netErrorLiveData.observe(
            this
        ) { t ->
            loadingView.isRefreshing = false
            Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(this)
        cateInfoAdapter = CateInfoAdapter()
        recyclerView.let {
            it.setHasFixedSize(false)
            it.layoutManager = manager
            it.adapter = cateInfoAdapter
        }

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private fun updateView(t: List<MenuCateInfo>?) {
        cateInfoAdapter?.setData(t)
        cateInfoAdapter?.notifyDataSetChanged()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MenuViewModel::class.java]
    }
}