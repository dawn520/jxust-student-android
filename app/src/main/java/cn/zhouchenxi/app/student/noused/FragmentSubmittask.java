package cn.zhouchenxi.app.student.noused;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.zhouchenxi.app.student.R;
import cn.zhouchenxi.app.student.noused.Task;

/**
 * Created by neokree on 16/12/14.
 */
public class FragmentSubmittask extends Fragment{

    private MyAdapter myAdapter;
    private List<Task> task = new ArrayList<>();
    private Context zcx = this.getActivity();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("AAAAAAAAAA____onCreateView");
        View rootview = inflater.inflate(
                R.layout.fragment_task, container, false);
        RecyclerView rv = (RecyclerView)rootview. findViewById(R.id.rv_recyclerview_data);
//        setupRecyclerView(rv);
//        //下拉刷新
//        SwipeRefreshLayout swipe = (SwipeRefreshLayout)rootview. findViewById(R.id.swipe_refresh_widget);
//        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (true) {
//                    Toast.makeText(zcx, "下拉刷新了一下", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(zcx, "你点了一下", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        return rootview;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //数据来源
        task.add(new Task("综合素质测评", "5人已接受", "各辅导员、班主任要深入学习《江西理工大学学生综合素质测评办法（试行）》（见《学生手册》2013版），正确掌握学生综合素质测评工作具体内容和要求。各班主任要专门开展学生综合素质测评工作主题班会活动。", "2015年9月23日"));
        task.add(new Task("贫困生认定", "15人已接受", "各班主任要专门开展学生综合素质测评工作主题班会活动。", "2015年10月3日"));
        // 初始化自定义的适配器
        myAdapter = new MyAdapter(this.getActivity(),task);
        recyclerView.setAdapter(myAdapter);
        Log.e("地方20", "地方21");
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
    {

        private List<Task> task;
        public Context mContext;
        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;

        public MyAdapter( Context context , List<Task> task)
        {
            this.mContext = context;
            this.task = task;
        }

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
        {
            // 给ViewHolder设置布局文件
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tasksubmit_list_item, viewGroup, false);
            //设置点击效果
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder( ViewHolder viewHolder, int i )
        {
            // 给ViewHolder设置元素
            Task p = task.get(i);

            viewHolder.mTextView_title.setText(p.title);
            viewHolder.mTextView_accept.setText(p.accept);
            viewHolder.mTextView_content.setText(p.content);
            viewHolder.mTextView_time.setText(p.time);

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "你点了一下", Toast.LENGTH_SHORT).show();
                    // Context context = v.getContext();
                    //启动详情页
                    // Intent intent = new Intent(context, LoginActivity.class);
                    //intent.putExtra(LoginActivity.EXTRA_NAME, holder.mBoundString);

                    //   context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            // 返回数据总数
            return task == null ? 0 : task.size();
        }

        // 重写的自定义ViewHolder
        public static class ViewHolder
                extends RecyclerView.ViewHolder
        {
            public final TextView mTextView_title;
            public final TextView mTextView_accept;
            public final TextView mTextView_content;
            public final TextView mTextView_time;
            private final View mView;

            public ViewHolder( View view )
            {
                super(view);
                mView = view;
                mTextView_title = (TextView) view.findViewById(R.id.item_title);
                mTextView_accept = (TextView) view.findViewById(R.id.item_accept);
                mTextView_content = (TextView) view.findViewById(R.id.item_content);
                mTextView_time = (TextView) view.findViewById(R.id.item_time);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("AAAAAAAAAA____onAttach3");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AAAAAAAAAA____onCreate3");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("AAAAAAAAAA____onActivityCreated3");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("AAAAAAAAAA____onStart3");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("AAAAAAAAAA____onResume3");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("AAAAAAAAAA____onPause3");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("AAAAAAAAAA____onStop3");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("AAAAAAAAAA____onDestroyView3");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("AAAAAAAAAA____onDestroy3");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("AAAAAAAAAA____onDetach3");
    }
}
