package com.lc.im.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMUserInfo;
import com.lc.im.R;
import com.lc.im.MessageType;
import com.lc.im.UserInfoManager;
import com.lc.im.Util;
import com.lc.im.hyphenate.HyImClient;
import com.lc.im.model.ImInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created by lvchao 2023/5/8
 * describe: 简单的文案适配器
 */
public class SimpleChatAdapter extends RecyclerView.Adapter<SimpleChatAdapter.SimpleViewHolder> {
    private final List<ImInfo> content = new ArrayList<>();

    public SimpleChatAdapter(List<ImInfo> content) {
        this.content.addAll(content);
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_text, parent, false);
        return new SimpleViewHolder(itemView);
    }

    private String time(long time) {
//        long time = Long.parseLong(lo);

        Date date = new Date(time);

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sd.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        ImInfo imInfo = content.get(position);
        String contentText = "";
//        if (imInfo.getSource() == 2) {
//            contentText = imInfo.getText();
//        } else {
//            contentText = imInfo.getTarget().getText();
//        }
//        if (imInfo.getSource() == 2) {
//            contentText = imInfo.getText();
//        } else {
        contentText = imInfo.getEmMessage().getBody().toString();
        contentText = contentText + "时间：" + time(imInfo.getEmMessage().getMsgTime());

//        }

        HyImClient.INSTANCE.getHyUserInfoManager().getSingleUserInfo(imInfo.getEmMessage().getFrom(), new UserInfoManager.UserInfoListener<EMUserInfo>() {
            @Override
            public void getAllUserInfo(List<EMUserInfo> uinfos) {

            }

            @Override
            public void getSingleUserInfo(EMUserInfo emUserInfo) {
                Glide.with(holder.itemView.getContext()).load(emUserInfo.getAvatarUrl()).into(holder.imageRight);
                Glide.with(holder.itemView.getContext()).load(emUserInfo.getAvatarUrl()).into(holder.imageLeft);
            }

            @Override
            public void setSingleInfoSuccess(String type) {

            }

            @Override
            public void setUserInfoSuccess(String success) {

            }

            @Override
            public void onError(String error) {

            }
        }, false);
        int viewType = holder.getItemViewType();
        if (viewType == MessageType.TYPE_SELF) {
            holder.textLeft.setVisibility(View.GONE);
            holder.textLeft.setText("");
            holder.imageLeft.setVisibility(View.GONE);
            holder.textRight.setVisibility(View.VISIBLE);
            holder.textRight.setText(Util.parseHyMessageTxt(contentText));
            holder.imageRight.setVisibility(View.VISIBLE);
        } else {
            holder.textRight.setVisibility(View.GONE);
            holder.textRight.setText("");
            holder.imageRight.setVisibility(View.GONE);
            holder.textLeft.setVisibility(View.VISIBLE);
            holder.textLeft.setText(Util.parseHyMessageTxt(contentText));
            holder.imageLeft.setVisibility(View.VISIBLE);
        }
    }

    public void add(ImInfo newContent, boolean top) {
        if (top) {
            content.add(0, newContent);
            notifyItemInserted(0);
        } else {
            content.add(newContent);
            notifyItemInserted(content.size() - 1);
        }
    }


    @Override
    public int getItemCount() {
        return content.size();
    }

    @Override
    public int getItemViewType(int position) {
        ImInfo imInfo = content.get(position);
        return imInfo.isSelf() ? MessageType.TYPE_SELF : MessageType.TYPE_OTHER;
    }

    protected static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public TextView textLeft;
        public TextView textRight;

        public ImageView imageLeft;
        public ImageView imageRight;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            textLeft = itemView.findViewById(R.id.tv_text_left);
            textRight = itemView.findViewById(R.id.tv_text_right);

            imageLeft = itemView.findViewById(R.id.iv_user_icon_left);
            imageRight = itemView.findViewById(R.id.iv_user_icon_right);
        }
    }
}

