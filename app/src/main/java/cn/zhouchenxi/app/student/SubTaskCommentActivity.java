package cn.zhouchenxi.app.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.widget.EditText;
import com.rey.material.widget.SnackBar;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;
import cn.zhouchenxi.app.student.adapter.TaskCommentAdapter;
import cn.zhouchenxi.app.student.model.PostCommentModel;
import cn.zhouchenxi.app.student.model.TaskCommentModel;
import cn.zhouchenxi.app.student.rfInterface.WebService;
import cn.zhouchenxi.app.student.setting.globalData;
import cn.zhouchenxi.app.student.widget.Divider;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SubTaskCommentActivity extends AppCompatActivity implements  BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener {

    private TaskCommentAdapter mAdapter;
    private BGARefreshLayout mRefreshLayout;
    private List<TaskCommentModel.returndata> mDatas;
    private RecyclerView mDataRv;
    private EditText commentBox;
    private int tid;
    private int ttid;
    private String uid = globalData.uid;
    private String varifyCode = globalData.varifyCode;
    //定义Retrofit service
    private WebService service;
    //定义第一条任务和最后一天任务的全局变量
    private int fisrtId;
    private int lastId;
    //定义回复某条评论的id
    private int replyId = 0;
    private int commentToUid = 0;
    private String commentToAuthor = "";
    SnackBar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("======","周晨希");
        setContentView(R.layout.activity_task_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查看评论");

        setSupportActionBar(toolbar);
        //添加返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //返回按钮监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent main = getIntent();
        tid= main.getIntExtra("tid",0);
        ttid= main.getIntExtra("ttid",0);
        //设置菜单栏监听
        commentBox = (EditText)findViewById(R.id.task_comment_box);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //设置发送评论按钮
        ImageView commentSubmit = (ImageView)findViewById(R.id.task_comment_submit);
        commentSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                postTaskComment();

            }
        });
        //创建并配置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(globalData.siteUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(WebService.class);


        initRefreshLayout();
        initRecyclerView();


    }

    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.task_comment_refresh);
        mRefreshLayout.setDelegate(this);
        //幻灯片效果
        // mRefreshLayout.setCustomHeaderView(DataEngine.getCustomHeaderOrFooterView(getContext()), false);
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getApplicationContext(), true);
        stickinessRefreshViewHolder.setStickinessColor(Color.parseColor("#3dbff0"));
        stickinessRefreshViewHolder.setRotateDrawable(getResources().getDrawable(R.mipmap.bga_refresh_stickiness));
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
        // mRefreshLayout.beginRefreshing();
        mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    private void initRecyclerView() {
        mDataRv = (RecyclerView) findViewById(R.id.task_comment_data);
        //添加RecyclerView滑动监听
        mDataRv.addOnScrollListener(new ScrollListListener());
        //设置RecyclerView动画(引用第三方库)
        mDataRv.setItemAnimator(new ScaleInLeftAnimator(new OvershootInterpolator(1f)));
        mDataRv.getItemAnimator().setAddDuration(500);
        mDataRv.getItemAnimator().setRemoveDuration(500);
        mDataRv.getItemAnimator().setMoveDuration(500);
        mDataRv.getItemAnimator().setChangeDuration(500);

        mDataRv.addItemDecoration(new Divider(getApplicationContext()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(linearLayoutManager);

        mAdapter = new TaskCommentAdapter(getApplicationContext());
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setOnRVItemLongClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnItemChildLongClickListener(this);


        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);
        alphaAdapter.setDuration(500);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        mDataRv.setAdapter(alphaAdapter);
        loadTaskComment(0);


    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        loadTaskComment(1);


    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        loadTaskComment(2);
        return false;
    }


    @Override
    public void onRVItemClick(View v, int position) {
        commentBox.setHint("回复:"+mAdapter.getItem(position).author);
        replyId = mAdapter.getItem(position).tcid;
        commentToUid = mAdapter.getItem(position).fromuid;
        commentToAuthor = mAdapter.getItem(position).author;
        //设置评论框回去焦点并淡出软键盘
        commentBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) commentBox.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);


    }

    @Override
    public boolean onRVItemLongClick(View v, int position) {
        Toast.makeText(getApplicationContext(), "长按了条目 " + mAdapter.getItem(position).tcid, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onItemChildClick(View v, int position) {
        if (v.getId() == R.id.tv_item_swipe_delete) {
            mAdapter.removeItem(position);
        }
    }

    @Override
    public boolean onItemChildLongClick(View v, int position) {
        if (v.getId() == R.id.tv_item_swipe_delete) {
            Toast.makeText(getApplicationContext(), "长按了删除 " + mAdapter.getItem(position).tcid, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }





    /**
     * 因为Android-Universal-Image-Loader不支持RecyclerView滑动监听,所以自己写了个监听
     * 来自http://www.lai18.com/content/1424058.html
     * Created by xixi on 16/4/20.
     */
    private class ScrollListListener extends RecyclerView.OnScrollListener {

        ImageLoader imageLoader = MainActivity.mImageLoader.getInstance();

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    //正在滑动
                    imageLoader.pause();
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    //滑动停止
                    imageLoader.resume();
                    break;
            }
        }

    }

    /**
     * @param mode 0:初始化
     *             1:下拉刷新数据
     *             2:上划加载更多
     * @return
     */
    public void loadTaskComment(final int mode)  {
        int flag = 0;
        switch (mode){
            case 1:flag = fisrtId;break;
            case 2:flag = lastId;break;
            default:flag = 0;break;
        }
        //请求读取任务:retrofit RXJava模式
        //先异步执行doOnNext然后执行onNext,最后onCompleted或onError
        Observable<TaskCommentModel> loadSubTaskComment = service.loadSubTaskComment(Integer.parseInt(uid), varifyCode,tid,mode,flag);
        loadSubTaskComment.doOnNext(new Action1<TaskCommentModel>() {

            @Override
            public void call(TaskCommentModel response) {
                if (mode != 0){
                    try {
                        Thread.sleep(2000);//主线程暂停2秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }



            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TaskCommentModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e("xxxxxx", "33333333");


                    }

                    @Override
                    public void onError(Throwable e) {
                        if(mode!=0){
                            mRefreshLayout.endRefreshing();
                        }else {

                        }
                        Toast.makeText(getApplication(), "连接服务器失败", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();


                    }

                    @Override
                    public void onNext(final TaskCommentModel response) {
                        switch (mode) {
                            case 0://初始化
                                Log.e("xxxxxx", "2222222222");
                                if (Integer.parseInt(response.returnstate.toString()) == 1) {
                                    if (response.returndata != null) {
                                        mDatas = response.returndata;
                                        mAdapter.setDatas(mDatas);
                                        fisrtId = response.returndata.get(0).tcid;
                                        lastId = response.returndata.get(response.returndata.size() - 1).tcid;
                                    } else {
                                        Toast.makeText(getApplication(), "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplication(), response.returnmessage, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1://下拉刷新
                                mRefreshLayout.endRefreshing();
                                if (Integer.parseInt(response.returnstate.toString()) == 1) {
                                    if (response.returndata != null) {
                                        mDatas.addAll(0, response.returndata);
                                        //稍稍改写以添加第三方库的动画效果
                                        int num = response.returndata.size();
                                        mAdapter.notifyItemRangeInserted(0, num);
                                        mDataRv.scrollToPosition(0);
                                        fisrtId = response.returndata.get(0).tcid;
                                    }else{
                                        Toast.makeText(getApplication(), "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getApplication(), response.returnmessage, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2://上划自动加载
                                mRefreshLayout.endLoadingMore();
                                if (Integer.parseInt(response.returnstate.toString()) == 1) {
                                    if (response.returndata != null) {
                                        mAdapter.addDatas(response.returndata);
                                        lastId = response.returndata.get(response.returndata.size() - 1).tcid;
                                    } else {
                                        Toast.makeText(getApplication(), "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getApplication(), response.returnmessage, Toast.LENGTH_SHORT).show();

                                }
                                break;
                        }
                    }
                });


    }

    /**
     * 发送评论
     */
    public void postTaskComment()  {
        Observable<PostCommentModel> postSubTaskComment = service.postSubTaskComment(
                Integer.parseInt(uid), varifyCode,tid,replyId,commentToUid,commentBox.getText().toString(),ttid);
        postSubTaskComment.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PostCommentModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e("xxxxxx", "3333222444");


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getApplication(), "连接服务器失败", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(final PostCommentModel response) {
                        if (Integer.parseInt(response.returnstate.toString()) == 1) {
                            TaskCommentModel.returndata data;
                            if(commentToAuthor.equals("")){
                                data = new TaskCommentModel.returndata(
                                        commentBox.getText().toString(),"刚刚",globalData.userName,0,Integer.parseInt(uid));
                            }else{
                                data = new TaskCommentModel.returndata(
                                        "回复 @"+commentToAuthor+" "+commentBox.getText().toString(),"刚刚",globalData.userName,0,Integer.parseInt(uid));
                            }
                            List<TaskCommentModel.returndata> data1 = new ArrayList<>();
                            data1.add(data);
                            if(mDatas==null) {
                                mAdapter.setDatas(data1);
                            }else{
                                mDatas.addAll(0, data1);

                            }
                            mAdapter.notifyItemRangeInserted(0, 1);
                            mDataRv.scrollToPosition(0);
                            commentBox.setHint("请输入评论");
                            commentBox.setText("");
                            commentToAuthor = "";
                            Toast.makeText(getApplication(), "评论成功!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplication(), response.returnmessage, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_comment, menu);
        return true;
    }

    //toolbar菜单选项监听
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_qxpl:
                    replyId = 0;
                    commentToUid = 0;
                    commentToAuthor = "";
                    commentBox.setHint("请输入评论");




                    break;
            }

            return true;
        }
    };

}
