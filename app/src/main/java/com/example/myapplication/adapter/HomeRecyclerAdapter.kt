package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.HomeItemData

class HomeRecyclerAdapter(private val mData: List<HomeItemData>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder>() {
    private var itemClickListener //自定义接口
            : OnItemClickListener? = null

    fun setItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        //接口
        fun onItemClickMethod(view: View?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //将布局文件View转换被传递给RecyclerView封装好的ViewHolder
        //View  contView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout,null);
        val contView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return ViewHolder(contView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //建立起ViewHolder中视图与数据的关联
        holder.textView.text = mData[position].itemName
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val textView: TextView

        init {
            textView = itemView.findViewById(R.id.textView)
            textView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (itemClickListener != null) {
                textView.elevation = 80f
                itemClickListener!!.onItemClickMethod(view, layoutPosition)
                textView.elevation = 0f
            }
        }
    }
}