package com.lc.nativelib.display;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.nativelib.R;
import com.lc.nativelib.model.MessageInfo;
import com.lc.nativelib.model.MessageRecord;
import com.lc.nativelib.model.MessageShow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class AnrDisplayDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMessageQueue;
    private RecyclerView recordView;
    private TextView tvTitle;
    private TextView tvStack;
    private MessageShow messageShow;
    private MessageInfoAdapter messageInfoAdapter;

    private MsgRecordAdapter msgRecordAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_detail);
        messageShow = getIntent().getParcelableExtra("messageShow");
        initView();
        messageInfoAdapter.setData(messageShow.getRecords());
        if (messageShow != null && messageShow.getRecords() != null && messageShow.getRecords().peekFirst() != null) {
            assert messageShow.getRecords().peekFirst() != null;
            updateRecordView(messageShow.getRecords().peekFirst());
        }
        if (messageShow.getTraceMessage() != null) {
            tvTitle.setText(messageShow.getTraceMessage());
        }

        if (messageShow.getStackMessage() != null) {
            tvStack.setText(messageShow.getStackMessage());
        }
    }

    private void initView() {
        recyclerViewMessageQueue = findViewById(R.id.recyclerViewMessageQueue);
        messageInfoAdapter = new MessageInfoAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewMessageQueue.setLayoutManager(linearLayoutManager);
        recyclerViewMessageQueue.setAdapter(messageInfoAdapter);
        messageInfoAdapter.setItemClickListener(messageInfo -> updateRecordView(messageInfo));

        recordView = findViewById(R.id.recordView);
        msgRecordAdapter = new MsgRecordAdapter();
        recordView.setAdapter(msgRecordAdapter);
        recordView.setLayoutManager(new LinearLayoutManager(this));

        tvTitle = findViewById(R.id.tv_title);
        tvStack = findViewById(R.id.tv_stack);

    }

    private void updateRecordView(MessageInfo messageInfo) {
        msgRecordAdapter.setData(messageInfo.getRecords());
    }

    private class MsgRecordAdapter extends RecyclerView.Adapter<MsgRecordAdapter.MViewHolder> {
        private List<MessageRecord> records = new ArrayList<>();

        @NonNull
        @Override
        public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_record, parent,
                    false);
            return new MViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
            MessageRecord messageRecord = records.get(position);
            holder.callbackName.setText("callbackName：" + messageRecord.getCallbackName());
            holder.what.setText("what：" + messageRecord.getWhat());
            holder.handlerAddress.setText("handlerAddress：" + messageRecord.getHandlerAddress());
            holder.handlerName.setText("handlerName：" + messageRecord.getHandlerName());
            if (position % 2 == 0) {
                holder.itemView.setBackgroundColor(Color.GREEN);
            } else {
                holder.itemView.setBackgroundColor(Color.YELLOW);
            }
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        public void setData(List<MessageRecord> records) {
            this.records.clear();
            this.records.addAll(records);
            notifyDataSetChanged();
        }

        private class MViewHolder extends RecyclerView.ViewHolder {
            public TextView handlerName;
            public TextView what;
            public TextView callbackName;
            public TextView handlerAddress;


            public MViewHolder(@NonNull View itemView) {
                super(itemView);
                handlerName = itemView.findViewById(R.id.handlerName);
                what = itemView.findViewById(R.id.what);
                callbackName = itemView.findViewById(R.id.callbackName);
                handlerAddress = itemView.findViewById(R.id.handlerAddress);
            }
        }
    }


    private class MessageInfoAdapter extends RecyclerView.Adapter<MessageInfoAdapter.MViewHolder> {
        List<MessageInfo> messageInfos = new ArrayList<>();
        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @NonNull
        @Override
        public MessageInfoAdapter.MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_info,
                    parent, false);
            return new MessageInfoAdapter.MViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MessageInfoAdapter.MViewHolder holder, int position) {
            MessageInfo messageInfo = messageInfos.get(position);
            holder.msgType.setText("消息类型：" + MessageInfo.msgTypeToString(messageInfo.getMessageType()));
            holder.wallTime.setText("运行时间：" + messageInfo.getWallTime());
            holder.recordCount.setText("记录数量：" + messageInfo.getRecordCount());

            int type = messageInfo.getMessageType();
            int color = getBg(type);
            holder.background.setBackgroundColor(color);
        }

        /**
         * MSG_TYPE_NONE(0),
         * MSG_TYPE_INFO(1),
         * MSG_TYPE_WARN(2),
         * MSG_TYPE_ANR(3),
         * MSG_TYPE_GAP(4);
         *
         * @param type
         * @return
         */
        private int getBg(int type) {
            int result = Color.GREEN;
            switch (type) {
                case 0:
                case 1:
                    result = Color.GREEN;
                    break;
                case 2:
                    result = Color.YELLOW;
                    break;
                case 3:
                    result = Color.RED;
                    break;
                case 4:
                    result = Color.GRAY;
                    break;
            }
            return result;
        }

        @Override
        public int getItemCount() {
            return messageInfos.size();
        }

        public void setData(ArrayDeque<MessageInfo> messageInfos) {
            this.messageInfos.clear();
            this.messageInfos.addAll(messageInfos);
            notifyDataSetChanged();
        }

        private class MViewHolder extends RecyclerView.ViewHolder {
            public TextView msgType;
            public TextView wallTime;
            public TextView recordCount;
            public View background;


            public MViewHolder(@NonNull View itemView) {
                super(itemView);
                msgType = itemView.findViewById(R.id.msg_type);
                wallTime = itemView.findViewById(R.id.wallTime);
                recordCount = itemView.findViewById(R.id.recordCount);
                background = itemView.findViewById(R.id.background);
                itemView.setOnClickListener(v -> {
                    if (itemClickListener != null) {
                        itemClickListener.ItemClick(messageInfos.get(getLayoutPosition()));
                    }
                });
            }
        }
    }

    interface ItemClickListener {
        void ItemClick(MessageInfo messageInfo);
    }

}
