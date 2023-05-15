package com.lc.im.hyphenate.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.im.R;

/**
 * created by lvchao 2023/5/13
 * describe:
 */
public class ConversationHolder extends RecyclerView.ViewHolder {
    public TextView lastMsg;
    public TextView tvNickName;

    public TextView tvUnReadCount;
    public ImageView ivUserIcon;


    public ConversationHolder(@NonNull View itemView) {
        super(itemView);
        lastMsg = itemView.findViewById(R.id.tv_top_msg);
        tvNickName = itemView.findViewById(R.id.tv_nickname);
        ivUserIcon = itemView.findViewById(R.id.iv_user_icon);
        tvUnReadCount = itemView.findViewById(R.id.tv_unread_count);
    }
}
