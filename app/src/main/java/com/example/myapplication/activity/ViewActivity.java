package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

/**
 * created by lvchao 2019-12-12
 * describe: 西瓜2面面试 快手1面面试 相同方向的滑动冲突问题
 */
public class ViewActivity extends AppCompatActivity {
////    BottomSheetDialog bottomSheetDialog;
//    private List<String> stringList = new ArrayList<>();
//    RecyclerView recyclerView;
////    BottomSheetAdapter bottomSheetAdapter;
//    BottomSheetBehavior mDialogBehavior;
//    ChildView childView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
//        Button luncher = findViewById(R.id.luncher);
////        luncher.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                showDialog();
////            }
////        });
//        for (int i = 0; i < 50; i++) {
//            stringList.add("demo" + i);
//        }
//        childView = findViewById(R.id.childView);
//        childView.setAdapter(new ListViewApapter());


    }

    /**
     * 关于内存优化
     *
     * 几个方面
     * 1、减小apk体积
     * 2、避免内存抖动，因为频繁的GC会影响性能
     * 3、解决内存泄漏，配合LeakCanary
     * 4、处理图片问题，当然网络图片Glide帮我处理好了，本地的图片资源呢？
     * 5、使用优化过的数据结构例如SparseMap
     * 6、谨慎使用三方库，然后一些大内存分配尽量catch一下，另外合理使用弱引和其他
     * 项目中做了哪些
     * 1、使用了内联函数
     * 2、本地资源图片的优化
     * 3、apk压缩
     * 4、使用了SparseMap替代HashMap，使用了StringBuilder
     * 5、解决了内存泄漏
     *
     *
     * 关于启动优化
     *
     * 1、使用主题来掩盖白屏的问题，这个并没有真的改变启动速度
     * 2、优化Application的onCreate()中做的事情，还需要根绝需求或异步加载或延后加载。闪屏页面的2秒停留时间可以利用
     * 3、优化闪屏页面的布局
     *
     * 关于布局优化
     *
     * 1、合理使用Merage、ViewStub
     * 2、减少View的嵌套的层数，使用Hierarchy Viewer去检测
     * 3、还在能实现需求的情况下，尽量使用轻量级的ViewGroup，例如能使用LinearLayout实现绝不使用RelativeLayout
     *
     * 
     */

//    private void showDialog() {
//        if (bottomSheetDialog == null) {
//            bottomSheetDialog = new BottomSheetDialog(this, R.style.dialog);
//        }
//        View view = View.inflate(this, R.layout.dialog_bottomsheet, null);
//        recyclerView = view.findViewById(R.id.dialog_bottomsheet_rv_lists);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        bottomSheetAdapter = new BottomSheetAdapter(stringList, ViewActivity.this);
//        recyclerView.setAdapter(bottomSheetAdapter);
//        bottomSheetDialog.setContentView(view);
//        mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
//        mDialogBehavior.setPeekHeight(getWindowHeight());
//        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                    bottomSheetDialog.dismiss();
//                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//            }
//        });
//        bottomSheetDialog.show();
//    }

//    private int getWindowHeight() {
//        Resources res = ViewActivity.this.getResources();
//        DisplayMetrics displayMetrics = res.getDisplayMetrics();
//        return (displayMetrics.heightPixels / 3) * 2;
//    }

    class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ViewActivity.this).inflate(R.layout.item, parent, false);
                TextView textView = convertView.findViewById(R.id.itemTextView);
//                textView.setText(stringList.get(position));
            }
            return convertView;
        }
    }

}

//class BottomSheetAdapter extends RecyclerView.Adapter<ItemViewHolder> {
//
//    private List<String> list;
//    private Context context;
//
//    public BottomSheetAdapter(List<String> list, Context context) {
//        this.list = list;
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item, viewGroup, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
//        itemViewHolder.textView.setText(list.get(i));
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//}

