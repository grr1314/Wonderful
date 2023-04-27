package com.lc.nativelib.display;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.nativelib.file.FileManager;
import com.lc.nativelib.R;
import com.lc.nativelib.model.MessageShow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnrDisplayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AnrRecordAdapter anrRecordAdapter;
    private String content;
    FileManager fileManager = new FileManager(new Gson());
    private ExecutorService singleThreadExecutor;

    private ExecutorService getSingleThreadExecutor() {
        if (singleThreadExecutor == null)
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        return singleThreadExecutor;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anr_record);
        initView();
        if (savedInstanceState != null) {
            content = savedInstanceState.getString("visibleLeakRefKey");
        } else {
            content = getIntent().getStringExtra("content");
        }
        fileManager.createDir(FileManager.ANR_DIR_PATH);
        List<MessageShow> messageShowList = fileManager.getGson().fromJson(content, new TypeToken<List<MessageShow>>() {
        }.getType());
        if (messageShowList != null && !messageShowList.isEmpty()) {
            updateView(messageShowList);
        } else {
            getSingleThreadExecutor().execute(() -> {
                List<MessageShow> list = new ArrayList<>();
                File[] tempList = fileManager.dir.listFiles();
                if (tempList == null) return;
                for (File targetFile : tempList) {
                    String content = fileManager.readFromFile(targetFile);
                    MessageShow messageShow = fileManager.getGson().fromJson(content, MessageShow.class);
                    list.add(messageShow);
                }
                recyclerView.post(() -> updateView(list));
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateView(List<MessageShow> messageShowList) {
        anrRecordAdapter.setData(messageShowList);
    }

    private void initView() {
        recyclerView = findViewById(R.id.anr_record);
        anrRecordAdapter = new AnrRecordAdapter();
        anrRecordAdapter.setItemClickListener(messageShow -> {
            Intent intent = new Intent(AnrDisplayActivity.this, AnrDisplayDetailActivity.class);
            intent.putExtra("messageShow", (Parcelable) messageShow);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(anrRecordAdapter);
    }

    public static PendingIntent createPendingIntent(Context context, List<MessageShow> messageShowList, String fileName
            , String content) {
        Intent intent = new Intent(context, AnrDisplayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("content", content);
        return PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT);
    }

    private class AnrRecordAdapter extends RecyclerView.Adapter<AnrRecordAdapter.MViewHolder> {
        List<MessageShow> messageShowList = new ArrayList<>();
        ItemClickListener itemClickListener;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @NonNull
        @Override
        public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anr_record,
                    parent, false);
            return new MViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MViewHolder holder, int position) {
            holder.name.setText(messageShowList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return messageShowList.size();
        }

        public void setData(List<MessageShow> messageShowList) {
            this.messageShowList.clear();
            this.messageShowList.addAll(messageShowList);
            notifyDataSetChanged();
        }

        private class MViewHolder extends RecyclerView.ViewHolder {
            public TextView name;

            public MViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.record_name);
                itemView.setOnClickListener(v -> {
                    if (itemClickListener != null) {
                        itemClickListener.ItemClick(messageShowList.get(getLayoutPosition()));
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("visibleLeakRefKey", content);
    }

    interface ItemClickListener {
        void ItemClick(MessageShow messageShow);
    }
}
