package com.lc.im.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mylibrary.BaseActivity;
import com.hyphenate.chat.EMConversation;
import com.lc.im.R;
import com.lc.im.hyphenate.HyConversationManager;
import com.lc.im.hyphenate.HyImClient;
import com.lc.im.hyphenate.adapter.ConversationAdapter;
import com.lc.im.hyphenate.listener.ConversationListener;
import com.lc.im.model.HyConversationInfo;
import com.lc.routerlib.core.ZRouter;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * created by lvchao 2023/5/12
 * describe: 会话列表
 */
public class HyConversationListActivity extends BaseActivity implements ConversationListener<HyConversationInfo> {
    private ConversationAdapter conversationAdapter;
    private RecyclerView recyclerView;
    private List<HyConversationInfo> conversationList = new ArrayList<>();
    MMKV kv = MMKV.defaultMMKV();

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
        swipeRefreshLayout = findViewById(R.id.loading);
        HyConversationManager conversationManager = HyImClient.INSTANCE.getHyConversationManager();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            if (conversationManager != null)
                conversationManager.getAllConversationsWithInfo(HyConversationListActivity.this);
        });

        if (conversationManager != null) {
            conversationManager.getAllConversationsWithInfo(this);
        }
        recyclerView = findViewById(R.id.rv_conversation_list);
        conversationAdapter = new ConversationAdapter();
        recyclerView.setAdapter(conversationAdapter);
        conversationAdapter.setData(conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationAdapter.setConversationItemClick(emConversation -> {
            ZRouter.newInstance().setPageName("chat").setTradeLine("im").setAction("jump").putParams("targetUserID", emConversation.getConversationId()).putParams("userID", kv.decodeString("userId")).putParams("nickname", emConversation.getNickName()).navigation(this);
        });
    }

    @Override
    public void startLoad() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void loadSuccess(List<HyConversationInfo> hyConversationInfos) {
        for (HyConversationInfo hyConversationInfo : hyConversationInfos) {
            conversationAdapter.addData(hyConversationInfo);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
