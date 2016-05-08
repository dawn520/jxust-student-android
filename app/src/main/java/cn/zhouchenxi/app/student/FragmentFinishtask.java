package cn.zhouchenxi.app.student;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;
import cn.zhouchenxi.app.student.adapter.TaskAdapter;
import cn.zhouchenxi.app.student.dao.DaoSession;
import cn.zhouchenxi.app.student.dao.TaskDao;
import cn.zhouchenxi.app.student.dbmodel.Task;
import cn.zhouchenxi.app.student.model.TaskModel;
import cn.zhouchenxi.app.student.rfInterface.WebService;
import cn.zhouchenxi.app.student.setting.globalData;
import cn.zhouchenxi.app.student.widget.Divider;
import de.greenrobot.dao.query.QueryBuilder;
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

/**
 * Created by neokree on 16/12/14.
 */
public class FragmentFinishtask extends Fragment  implements  BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener {


    private String siteurl = globalData.siteUrl;
    public static TaskAdapter mAdapter;
    private BGARefreshLayout mRefreshLayout;
    private List<TaskModel.returndata> mDatas;
    private RecyclerView mDataRv;
    public static View rootview;

    private DaoSession daoSession;
    private TaskDao taskDao;
    //定义第一条任务和最后一条任务的全局变量
    private int fisrtId;
    private int lastId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_task_recyclerview, container, false);
        initRefreshLayout();
        initRecyclerView();
        return rootview;
    }

    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout) rootview.findViewById(R.id.rl_recyclerview_refresh);
        mRefreshLayout.setDelegate(this);
        //幻灯片效果
        // mRefreshLayout.setCustomHeaderView(DataEngine.getCustomHeaderOrFooterView(getContext()), false);
        BGAStickinessRefreshViewHolder stickinessRefreshViewHolder = new BGAStickinessRefreshViewHolder(getContext(), true);
        stickinessRefreshViewHolder.setStickinessColor(Color.parseColor("#3dbff0"));
        stickinessRefreshViewHolder.setRotateDrawable(getResources().getDrawable(R.mipmap.bga_refresh_stickiness));
        mRefreshLayout.setRefreshViewHolder(stickinessRefreshViewHolder);
      //  mRefreshLayout.beginRefreshing();
        mRefreshLayout.setIsShowLoadingMoreView(false);
    }

    private void initRecyclerView() {
        mDataRv = (RecyclerView) rootview.findViewById(R.id.rv_recyclerview_data);
        //添加RecyclerView滑动监听
        mDataRv.addOnScrollListener(new ScrollListListener());
        //设置RecyclerView动画(引用第三方库)
        mDataRv.setItemAnimator(new ScaleInLeftAnimator(new OvershootInterpolator(1f)));
        mDataRv.getItemAnimator().setAddDuration(500);
        mDataRv.getItemAnimator().setRemoveDuration(500);
        mDataRv.getItemAnimator().setMoveDuration(500);
        mDataRv.getItemAnimator().setChangeDuration(500);

        mDataRv.addItemDecoration(new Divider(getContext()));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDataRv.setLayoutManager(linearLayoutManager);

        mAdapter = new TaskAdapter(getContext());
        mAdapter.setOnRVItemClickListener(this);
        mAdapter.setOnRVItemLongClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setOnItemChildLongClickListener(this);

        ScaleInAnimationAdapter alphaAdapter = new ScaleInAnimationAdapter(mAdapter);
        alphaAdapter.setDuration(500);
        alphaAdapter.setInterpolator(new OvershootInterpolator());
        mDataRv.setAdapter(alphaAdapter);

        daoSession = CustomApplication.getDaoSession(this.getContext()); // 获取DaoSession
        taskDao = daoSession.getTaskDao(); // 获取taskDao 可对Task进行操作
        //读取任务
        loadTask(0,3);

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //原始提供的AsyncTask模式
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                mRefreshLayout.endRefreshing();
//                mDatas.addAll(0, DataEngine.loadNewData());
//                //稍稍改写以添加第三方库的动画效果
//                int num = DataEngine.loadNewData().size();
//                mAdapter.notifyItemRangeInserted(0, num);
//                mDataRv.scrollToPosition(0);
//                //mAdapter.setDatas(mDatas);
//
//            }
//        }.execute();

        //采用retrofit2
        //读取任务
        loadTask(1,3);



    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //读取任务
        loadTask(2,3);
        return true;
    }

    @Override
    public void onRVItemClick(View v, int position) {
        Intent intent = new Intent(MainActivity.mainActivity, TaskActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("tid",mAdapter.getItem(position).tid);
        bundle.putInt("taskType",3);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onRVItemLongClick(View v, int position) {
        Toast.makeText(getContext(), "长按了条目 " + mAdapter.getItem(position).title, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "长按了删除 " + mAdapter.getItem(position).title, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    /**
     * @param mode 0:初始化
     *             1:下拉刷新数据
     *             2:上划加载更多
     * @param type 0:最新任务
     *             1:已接受任务
     *             2:已提交任务
     *             3:已审核任务
     * @return
     */
    public void loadTask(final int mode, int type)  {
        String uid = globalData.uid;
        String varifyCode = globalData.varifyCode;
        //创建并配置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(siteurl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        WebService service = retrofit.create(WebService.class);
        int flag = 0;
        switch (mode){
            case 1:flag = fisrtId;break;
            case 2:flag = lastId;break;
            default:flag = 0;break;
        }
        //请求读取任务:retrofit RXJava模式
        //先异步执行doOnNext然后执行onNext,最后onCompleted或onError
        Observable<TaskModel> loadTask = service.loadTask(Integer.parseInt(uid), varifyCode, mode, type,flag);
        loadTask.doOnNext(new Action1<TaskModel>() {

            @Override
            public void call(TaskModel response) {
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
                .subscribe(new Subscriber<TaskModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e("xxxxxx", "33333333");


                    }

                    @Override
                    public void onError(Throwable e) {
                        if(mode!=0){
                            mRefreshLayout.endRefreshing();
                        }else {
                            loadFormDb();
                        }
                        Toast.makeText(getContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();

                        e.printStackTrace();


                    }

                    @Override
                    public void onNext(final TaskModel response) {
                        switch (mode) {
                            case 0://初始化
                                Log.e("xxxxxx", "2222222222");
                                if (Integer.parseInt(response.returnstate.toString()) == 1) {
                                    if (response.returndata != null) {
                                        mDatas = response.returndata;
                                        mAdapter.setDatas(mDatas);
                                        fisrtId = response.returndata.get(0).tid;
                                        lastId = response.returndata.get(response.returndata.size() - 1).tid;
                                    } else {
                                        Toast.makeText(getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    loadFormDb();
                                    Toast.makeText(getContext(), response.returnmessage, Toast.LENGTH_SHORT).show();
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
                                        fisrtId = response.returndata.get(0).tid;
                                    }else{
                                        Toast.makeText(getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getContext(), response.returnmessage, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2://上划自动加载
                                mRefreshLayout.endLoadingMore();
                                if (Integer.parseInt(response.returnstate.toString()) == 1) {
                                    if (response.returndata != null) {
                                        mAdapter.addDatas(response.returndata);
                                        lastId = response.returndata.get(response.returndata.size() - 1).tid;
                                    } else {
                                        Toast.makeText(getContext(), "没有更多数据", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getContext(), response.returnmessage, Toast.LENGTH_SHORT).show();

                                }
                                break;
                        }
                    }
                });

//        //请求读取任务:retrofit常规call模式
//        Call<TaskModel> loadTask = service.loadTask(Integer.parseInt(uid), varifyCode, mode, type);
//        Log.e("77777", "669666");
//        loadTask.enqueue(new Callback <TaskModel>() {
//            @Override
//            public void onResponse(Call<TaskModel> call, Response<TaskModel> response) {
//                if (response.isSuccessful()) {
//                    TaskModel body = response.body();
//                    if (Integer.parseInt(body.returnstate.toString()) == 1) {
//                        mDatas = body.returndata;
//                        mAdapter.setDatas(mDatas);
//                        MainActivity.connectdialog.dismiss();
//                    } else {
//                        MainActivity.connectdialog.setTitle("提示");
//                        MainActivity.connectdialog.setContent("获取数据失败:" + body.returnmessage);
//                    }
//                } else {
//                    int statusCode = response.code();
//                    MainActivity.connectdialog.setTitle("提示");
//                    MainActivity.connectdialog.setContent("连接服务器失败,返回代码:" + statusCode);
//
//                    // handle request errors yourself
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TaskModel> call, Throwable t) {
//                MainActivity.connectdialog.setTitle("提示");
//                MainActivity.connectdialog.setContent("连接服务器失败:请检查网络");
//            }
//
//        });


//
//
//
//
//
//
//        returnMap =  connect.getData(siteurl+"/index.php/Home/Client/loadTask",tomap);
//        String returnMessage;
//        if(Integer.parseInt(returnMap.get("state").toString())==1){
//            String result = returnMap.get("data").toString();
//            JSONObject jsonObject = new JSONObject(result);
//            int returnState = jsonObject.getInt("returnstate");
//            returnMessage = jsonObject.getString("returnmessage");
//            String restrunData = jsonObject.getString("returndata");
//            if (returnState == 1) {
//                Gson gson = new Gson();
//                List<TaskModel> returnList = gson.fromJson(restrunData,new TypeToken<List<TaskModel>>(){}.getType());
//                for(int i = 0; i < returnList.size() ; i++)
//                {
//                    TaskModel p = returnList.get(i);
//                    taskList.add(p);
//                }
//                mDatas = taskList;
//                mAdapter.setDatas(mDatas);
//            }
//        } else {
//            returnMessage = "连接服务器失败";
//        }
//
//        return returnMessage;

    }



    /**
     * 因为Android-Universal-Image-Loader不支持RecyclerView滑动监听,所以自己写了个监听
     * 来自http://www.lai18.com/content/1424058.html
     * Created by xixi on 16/4/20.
     */
    private class ScrollListListener extends RecyclerView.OnScrollListener {

        ImageLoader imageLoader= MainActivity.mImageLoader.getInstance();
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            switch (newState){
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

//        @Override
//        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            //请忽视这个方法,这个方法是我自己使用的,判断向上,向下滑动的
//            /**
//             * 向下滑动 dy为负数 false
//             * 向上滑动 dy为正数 true
//             */
//            if (dy > 0) {
//                TScrollState.upAndDownState(true);
//            } else {
//                TScrollState.upAndDownState(false);
//            }
//
//        }

    }


    public void loadFormDb(){
        QueryBuilder<Task> startQuery = taskDao.queryBuilder()
                .limit(5)
                .orderDesc(TaskDao.Properties.Id);
        List<Task> taskList = startQuery.list(); // 使用list进行查询

        if(taskList.size()==0){



        }else{
            List<TaskModel.returndata> showList = new ArrayList<>();
            for(int i =0;i<taskList.size();i++){
                Task p = taskList.get(i);
                TaskModel.returndata q =new TaskModel.returndata(p.getTitle(),p.getDetail(),
                        p.getTime(),p.getAuthor(),p.getId().intValue(),p.getFromuid());
                showList.add(q);
            }
            Log.e("行数","+"+taskList.size());
            mDatas = showList;
            mAdapter.setDatas(mDatas);
            fisrtId = showList.get(0).tid;
            lastId = showList.get(showList.size()-1).tid;
        }
    }










}
