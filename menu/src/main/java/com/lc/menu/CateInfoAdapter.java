package com.lc.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lc.repository.model.MenuCateInfo;

import java.util.ArrayList;
import java.util.List;

public class CateInfoAdapter extends RecyclerView.Adapter<CateInfoAdapterHolder> {
    List<MenuCateInfo> data = new ArrayList<>();


    public void setData(List<MenuCateInfo> data) {
        this.data.clear();
        addData(data);
    }

    public void addData(List<MenuCateInfo> data) {
        this.data.addAll(data);
    }

    @NonNull
    @Override
    public CateInfoAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cate_info_item, parent, false);
        return new CateInfoAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CateInfoAdapterHolder holder, int position) {
        holder.textView.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
