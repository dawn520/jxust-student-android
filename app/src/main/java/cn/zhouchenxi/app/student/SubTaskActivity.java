package cn.zhouchenxi.app.student;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import cn.zhouchenxi.app.student.model.SubTaskItemModel;
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

public class SubTaskActivity extends AppCompatActivity {

    private WebView webview;
    private MaterialDialog connectdialog;
    private String siteurl = globalData.siteUrl;
    private int subId;
    private int ttid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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

        //设置进度
        connectdialog = new MaterialDialog.Builder(SubTaskActivity.this)
                .content("读取中~")
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelListener(new DialogInterface.OnCancelListener(){
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(SubTaskActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .show();


        webview = (WebView) findViewById(R.id.taskContent);

        // 创建并配置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(siteurl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        final WebService service = retrofit.create(WebService.class);

        final Intent main = getIntent();
        subId = main.getIntExtra("subId",0);
        ttid = main.getIntExtra("ttid",0);
        Log.e("ggggggg","000000"+ttid);

        //读取任务数据
        Observable<SubTaskItemModel> subTaskItem = service.subTaskItem(Integer.parseInt(globalData.uid), globalData.varifyCode,subId);
        subTaskItem.doOnNext(new Action1<SubTaskItemModel>() {

            @Override
            public void call(SubTaskItemModel response) {





            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SubTaskItemModel>() {
                    @Override
                    public void onCompleted() {
                        Log.e("xxxxxx", "33333333");


                    }

                    @Override
                    public void onError(Throwable e) {
                        connectdialog.dismiss();
                        e.printStackTrace();
                        new MaterialDialog.Builder(SubTaskActivity.this)
                                .title("提示")
                                .content("连接服务器失败,请重试!")
                                .negativeText("取消")
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        Toast.makeText(SubTaskActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    }
                                })
                                .show();

                    }

                    @Override
                    public void onNext(final SubTaskItemModel response) {
                        connectdialog.dismiss();
                        if (Integer.parseInt(response.returnstate.toString()) == 1) {
                            TextView authorView = (TextView) findViewById(R.id.taskAuthor);
                            authorView.setText(response.returndata.author);
                            TextView timeView = (TextView) findViewById(R.id.taskTime);
                            timeView.setText(response.returndata.time);
                            //加载需要显示的网页
                            webview.loadDataWithBaseURL(null, response.returndata.content, "text/html", "utf-8", null);
                            //设置WebView属性，能够执行Javascript脚本
                            webview.getSettings().setJavaScriptEnabled(true);
                            toolbar.setTitle("提交的内容");
                        } else {
                            new MaterialDialog.Builder(SubTaskActivity.this)
                                    .title("提示")
                                    .content(response.returnmessage)
                                    .negativeText("取消")
                                    .dismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            Toast.makeText(SubTaskActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    })
                                    .show();


                        }


                    }
                });







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(SubTaskActivity.this, SubTaskCommentActivity.class);
                Bundle commentBundle=new Bundle();
                commentBundle.putInt("tid",subId);
                commentBundle.putInt("ttid",ttid);
                commentIntent.putExtras(commentBundle);
                startActivity(commentIntent);
            }
        });
    }

}
