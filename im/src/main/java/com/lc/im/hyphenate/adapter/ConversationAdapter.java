package com.lc.im.hyphenate.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.lc.im.R;
import com.lc.im.model.HyConversationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * created by lvchao 2023/5/13
 * describe:
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationHolder> {
    private List<HyConversationInfo> conversationList;

    private ConversationItemClick conversationItemClick;

    public void setConversationItemClick(ConversationItemClick conversationItemClick) {
        this.conversationItemClick = conversationItemClick;
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_conversation, parent, false);
        return new ConversationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {

        HyConversationInfo hyConversationInfo = conversationList.get(position);
        holder.lastMsg.setText(hyConversationInfo.getLastMsgContent());
        holder.tvNickName.setText(hyConversationInfo.getNickName());
        holder.tvUnReadCount.setText(hyConversationInfo.getUnReadCount());
        Glide.with(holder.itemView.getContext()).load(hyConversationInfo.getEmUserInfo().getAvatarUrl()).into(holder.ivUserIcon);
        holder.itemView.setOnClickListener(view -> {
            if (conversationItemClick != null) {
                conversationItemClick.onItemConversationClick(hyConversationInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void setData(List<HyConversationInfo> conversationList) {
        if (this.conversationList == null) {
            this.conversationList = new ArrayList<>();
        }
        this.conversationList.clear();
        this.conversationList.addAll(conversationList);
    }

    public void addData(HyConversationInfo conversation) {
        if (this.conversationList == null) {
            this.conversationList = new ArrayList<>();
        }
        this.conversationList.add(conversation);
        notifyItemInserted(0);
    }


    public interface ConversationItemClick {
        void onItemConversationClick(HyConversationInfo hyConversationInfo);
    }
}
