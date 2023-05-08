package com.lc.im.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.im.R;
import com.lc.im.model.ImInfo;

import java.util.ArrayList;
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
//        if (viewType == MessageType.TYPE_SELF) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_chat_me, parent, false);
//            return new MeViewHolder(view);
//        } else {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_text, parent, false);
        return new SimpleViewHolder(itemView);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        String contentText = content.get(position).getTarget().getText();
        int viewType = holder.getItemViewType();
        if (viewType == MessageType.TYPE_SELF) {
            holder.textLeft.setVisibility(View.GONE);
            holder.textLeft.setText("");
            holder.imageLeft.setVisibility(View.GONE);
            holder.textRight.setVisibility(View.VISIBLE);
            holder.textRight.setText(contentText);
            holder.imageRight.setVisibility(View.VISIBLE);
        } else {
            holder.textRight.setVisibility(View.GONE);
            holder.textRight.setText("");
            holder.imageRight.setVisibility(View.GONE);
            holder.textLeft.setVisibility(View.VISIBLE);
            holder.textLeft.setText(contentText);
            holder.imageLeft.setVisibility(View.VISIBLE);
        }
    }

    public void add(ImInfo newContent) {
        content.add(newContent);
        notifyItemInserted(content.size() - 1);
    }


    @Override
    public int getItemCount() {
        return content.size();
    }

    @Override
    public int getItemViewType(int position) {
        ImInfo imInfo = content.get(position);
        int type = imInfo.getPeerId().equals("123456") ? MessageType.TYPE_SELF : MessageType.TYPE_OTHER;
        return type;
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

