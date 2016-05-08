package cn.zhouchenxi.app.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.rey.material.widget.Button;

import cn.zhouchenxi.app.student.model.PostCommentModel;
import cn.zhouchenxi.app.student.model.TaskItemModel;
import cn.zhouchenxi.app.student.rfInterface.WebService;
import cn.zhouchenxi.app.student.setting.globalData;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TaskActivity extends AppCompatActivity {

    private WebView webview;
    private MaterialDialog connectdialog;
    private String siteurl = globalData.siteUrl;
    private int subId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        // 创建并配置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(siteurl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final WebService service = retrofit.create(WebService.class);

        final Intent main = getIntent();
        final int tid = main.getIntExtra("tid",0);
        final int taskType = main.getIntExtra("taskType",0);
        final int position = main.getIntExtra("position",0);
        final Button taskButtonJsrw = (Button) findViewById(R.id.taskButtonJsrw);
        Button taskButtonTjrw = (Button) findViewById(R.id.taskButtonTjrw);
        taskButtonJsrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //首先按钮不可再次点击
                taskButtonJsrw.setEnabled(false);
                //提交任务
                Observable<PostCommentModel> jsTask = service.jsTask(Integer.parseInt(globalData.uid), globalData.varifyCode,tid);
                jsTask.doOnNext(new Action1<PostCommentModel>() {

                    @Override
                    public void call(PostCommentModel response) {





                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<PostCommentModel>() {
                            @Override
                            public void onCompleted() {
                                Log.e("xxxxxx", "33333333");
                            }

                            @Override
                            public void onError(Throwable e) {
                                connectdialog.dismiss();
                                e.printStackTrace();
                                new MaterialDialog.Builder(TaskActivity.this)
                                        .title("提示")
                                        .content("连接服务器失败,请重试!")
                                        .negativeText("取消")
                                        .show();
                            }

                            @Override
                            public void onNext(final PostCommentModel response) {
                                connectdialog.dismiss();
                                if (Integer.parseInt(response.returnstate.toString()) == 1) {
                                    taskButtonJsrw.setText("任务已接受");
                                    switch (taskType) {
                                        case 0:
                                            FragmentNewtask.mAdapter.removeItem(position);
                                            break;
                                        case 1:
                                            FragmentAcceptedtask.mAdapter.removeItem(position);
                                            break;
                                        case 2:
                                            FragmentSubmitedtask.mAdapter.removeItem(position);
                                            break;
                                        case 3:
                                            FragmentFinishtask.mAdapter.removeItem(position);
                                            break;
                                    }
                                    new MaterialDialog.Builder(TaskActivity.this)
                                            .title("提示")
                                            .content(response.returnmessage)
                                            .negativeText("确定")
                                            .show();
                                }else{
                                    taskButtonJsrw.setEnabled(true);
                                    new MaterialDialog.Builder(TaskActivity.this)
                                            .title("提示")
                                            .content(response.returnmessage)
                                            .negativeText("取消")
                                            .show();
                                }
                            }
                        });
            }
        });




        switch (taskType) {
            case 0:
                taskButtonJsrw.setVisibility(View.VISIBLE);
                break;
            case 1:
                taskButtonJsrw.setText("任务已接受");
                taskButtonJsrw.setEnabled(false);
                taskButtonJsrw.setBackgroundColor(Color.parseColor("#2196F3"));
                taskButtonJsrw.setVisibility(View.VISIBLE);

//                taskButtonTjrw.setBackgroundColor(Color.parseColor("#FFCC00"));
//                taskButtonTjrw.setVisibility(View.VISIBLE);
                break;
            case 2:
                taskButtonJsrw.setText("等待审核");
                taskButtonJsrw.setBackgroundColor(Color.parseColor("#CCCCCC"));
                taskButtonJsrw.setVisibility(View.VISIBLE);
                taskButtonJsrw.setEnabled(false);

                taskButtonTjrw.setText("查看提交的内容");
                taskButtonTjrw.setBackgroundColor(Color.parseColor("#FFCC00"));
                taskButtonTjrw.setVisibility(View.VISIBLE);
                taskButtonTjrw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TaskActivity.this, SubTaskActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("subId",subId);
                        bundle.putInt("ttid",tid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                break;
            case 3:
                taskButtonJsrw.setText("任务已完成");
                taskButtonJsrw.setEnabled(false);
                taskButtonJsrw.setBackgroundColor(Color.parseColor("#FF0033"));
                taskButtonJsrw.setVisibility(View.VISIBLE);

                taskButtonTjrw.setText("查看提交的内容");
                taskButtonTjrw.setBackgroundColor(Color.parseColor("#FFCC00"));
                taskButtonTjrw.setVisibility(View.VISIBLE);
                taskButtonTjrw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TaskActivity.this, SubTaskActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putInt("subId",subId);
                        bundle.putInt("ttid",tid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                break;
        }


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        //设置评论按钮事件
        FloatingActionButton comment = (FloatingActionButton) findViewById(R.id.taskComment);
        comment.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskActivity.this, TaskCommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("tid",tid);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        //设置进度
        connectdialog = new MaterialDialog.Builder(TaskActivity.this)
                .content("读取中~")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelListener(new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(TaskActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .show();


        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        webview = (WebView) findViewById(R.id.taskContent);


        //读取任务数据
        Observable<TaskItemModel> taskiItem = service.taskItem(Integer.parseInt(globalData.uid), globalData.varifyCode,tid);
        taskiItem.doOnNext(new Action1<TaskItemModel>() {

            @Override
            public void call(TaskItemModel response) {





            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TaskItemModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e("xxxxxx", "33333333");


                    }

                    @Override
                    public void onError(Throwable e) {
                        connectdialog.dismiss();
                        e.printStackTrace();
                        new MaterialDialog.Builder(TaskActivity.this)
                                .title("提示")
                                .content("连接服务器失败,请重试!")
                                .negativeText("取消")
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Toast.makeText(TaskActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                })
                                .show();

                    }

                    @Override
                    public void onNext(final TaskItemModel response) {
                        connectdialog.dismiss();
                        if (Integer.parseInt(response.returnstate.toString()) == 1) {
                            if (response.returndata.title != null) {
                                subId = response.returndata.subid;
                                TextView titleView = (TextView) findViewById(R.id.taskTitle);
                                titleView.setText(response.returndata.title);
                                TextView authorView = (TextView) findViewById(R.id.taskAuthor);
                                authorView.setText(response.returndata.author);
                                TextView timeView = (TextView) findViewById(R.id.taskTime);
                                timeView.setText(response.returndata.time);
                                //加载需要显示的网页
                                webview.loadDataWithBaseURL(null, response.returndata.content, "text/html", "utf-8", null);
                                //设置WebView属性，能够执行Javascript脚本
                                webview.getSettings().setJavaScriptEnabled(true);
                                collapsingToolbar.setTitle(response.returndata.title);
                                if(response.returndata.substate==2){
                                        taskButtonJsrw.setText("审核不通过");
                                        taskButtonJsrw.setBackgroundColor(Color.parseColor("#FF0033"));
                                }
                            } else {
                                Log.e("rrrrr","rrrrr");
                                new MaterialDialog.Builder(TaskActivity.this)
                                        .title("提示")
                                        .content("读取任务失败,任务不存在")
                                        .negativeText("取消")
                                        .dismissListener(new DialogInterface.OnDismissListener() {
                                            @Override
                                            public void onDismiss(DialogInterface dialog) {
                                                Toast.makeText(TaskActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                            }
                                        })
                                        .show();
                            }

                        }else{
                            new MaterialDialog.Builder(TaskActivity.this)
                                    .title("提示")
                                    .content(response.returnmessage)
                                    .negativeText("取消")
                                    .dismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            Toast.makeText(TaskActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    })
                                    .show();


                        }


                    }
                });





        loadBackdrop();






        }


    //设置webview里的链接在浏览器中打开~
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(globalData.getRandomCheeseDrawable()).centerCrop().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task, menu);
        return true;
    }


}
