package com.lc.menu;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CateInfoAdapterHolder extends RecyclerView.ViewHolder {
    public TextView textView;

    public CateInfoAdapterHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.cate_name);
    }
}
