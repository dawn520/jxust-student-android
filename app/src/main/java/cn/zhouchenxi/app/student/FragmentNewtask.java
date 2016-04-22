package cn.zhouchenxi.app.student;

import android.graphics.Color;
import android.os.AsyncTask;
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
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildLongClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGAStickinessRefreshViewHolder;
import cn.zhouchenxi.app.student.adapter.TaskAdapter;
import cn.zhouchenxi.app.student.engine.DataEngine;
import cn.zhouchenxi.app.student.method.mapRead;
import cn.zhouchenxi.app.student.model.TaskModel;
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

/**
 * Created by neokree on 16/12/14.
 */
public class FragmentNewtask extends Fragment  implements  BGARefreshLayout.BGARefreshLayoutDelegate, BGAOnRVItemClickListener, BGAOnRVItemLongClickListener, BGAOnItemChildClickListener, BGAOnItemChildLongClickListener {


    public String siteurl = globalData.siteUrl;
    private TaskAdapter mAdapter;
    private BGARefreshLayout mRefreshLayout;
    private List<TaskModel.returndata> mDatas;
    private RecyclerView mDataRv;
    public static View rootview;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_recyclerview, container, false);
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


        //mDatas = DataEngine.loadInitDatas();



        if(MainActivity.connectdialog.isCancelled()){
            MainActivity.connectdialog.show();
        }
        //读取任务
        loadTask(0,0);

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mRefreshLayout.endRefreshing();
                mDatas.addAll(0, DataEngine.loadNewData());
                //稍稍改写以添加第三方库的动画效果
                int num = DataEngine.loadNewData().size();
                mAdapter.notifyItemRangeInserted(0, num);
                mDataRv.scrollToPosition(0);
                //mAdapter.setDatas(mDatas);

            }
        }.execute();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mRefreshLayout.endLoadingMore();
                mAdapter.addDatas(DataEngine.loadMoreData());

                // 稍稍改写以添加第三方库的动画效果
//                int num = mDatas.size();
//                mDatas.addAll(num, DataEngine.loadMoreData());
//                int added = mDatas.size();
//                mAdapter.notifyItemRangeInserted(num,added);
//                mDataRv.scrollToPosition(num);
            }
        }.execute();
        return true;
    }

    @Override
    public void onRVItemClick(View v, int position) {
        Toast.makeText(getContext(), "点击了条目 " + mAdapter.getItem(position).title, Toast.LENGTH_SHORT).show();
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
    public void loadTask(int mode, int type)  {
        final List<TaskModel.returndata> taskList = new ArrayList<>();
        HashMap<String, Object> loginMap = (HashMap<String, Object>) mapRead.getMsg("login", this.getContext());
        String uid = (String) loginMap.get("uid");
        String varifyCode = (String) loginMap.get("varifycode");
        //创建并配置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(siteurl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        WebService service = retrofit.create(WebService.class);
        //请求读取任务:retrofit RXJava模式
        //先执行doOnNext然后执行onNext,最后onCompleted或onError
        Observable<TaskModel> loadTask = service.loadTask(Integer.parseInt(uid), varifyCode, mode, type);
        Log.e("77777", "669666");
        loadTask.doOnNext(new Action1<TaskModel>() {

            @Override
            public void call(TaskModel taskModel) {
                Log.e("xxxxxx", "0111111");

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
                        Log.e("xxxxxx", "44444444");
                        MainActivity.connectdialog.setTitle("提示");
                        MainActivity.connectdialog.setContent("获取服务器响应失败");

                    }

                    @Override
                    public void onNext(TaskModel response) {
                        Log.e("xxxxxx", "2222222222");
                        if (Integer.parseInt(response.returnstate.toString()) == 1) {
                            mDatas = response.returndata;
                            mAdapter.setDatas(mDatas);
                            MainActivity.connectdialog.dismiss();
                        } else {
                            MainActivity.connectdialog.setTitle("提示");
                            MainActivity.connectdialog.setContent("获取数据失败:" + response.returnmessage);
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












}
