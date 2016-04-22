package cn.zhouchenxi.app.student;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import cn.zhouchenxi.app.student.model.TestModel;
import cn.zhouchenxi.app.student.rfInterface.WebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.155/jxust-student/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WebService service = retrofit.create(WebService.class);
       Call <List<TestModel>> repos = service.listRepos(1);
        repos.enqueue(new Callback <List<TestModel>>() {
            @Override
            public void onResponse(Call <List<TestModel>> call, Response <List<TestModel>> response) {
                List<TestModel> body = response.body();
                for (int i = 0; i < body.size(); i++){
                    TestModel p = body.get(i);
                    Log.e("88888", p.title.toString());
            }

            }

            @Override
            public void onFailure(Call <List<TestModel>> call, Throwable t) {
                Log.e("999999  ","999999");
            }

       });





//        final ImageView mImageView;
//        String imageUrl = "http://www.ijxust.com/Public/assets/admin/pages/media/gallery/image4.jpg";
//        mImageView = (ImageView) findViewById(R.id.imageView);
//
//
//        //创建默认的ImageLoader配置参数
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                .createDefault(this);
//        ImageLoader.getInstance().init(configuration);
//
//        //显示图片的配置
//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                 .showImageOnLoading(R.drawable.loading_spinner_blue)
//                 .showImageOnFail(R.drawable.error)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();
//        mImageView.setImageResource(R.drawable.profile);

       // ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);











//        Thread accessWebServiceThreadavatar = new Thread(new WebServiceHandleravatar());
//        accessWebServiceThreadavatar.start();



            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



}

