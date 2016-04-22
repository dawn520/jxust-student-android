package cn.zhouchenxi.app.student.engine;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.bgabanner.BGABanner;
import cn.zhouchenxi.app.student.MainActivity;
import cn.zhouchenxi.app.student.R;
import cn.zhouchenxi.app.student.model.TaskModel;


/**
 * 作者:周晨希
 * 创建时间:2015年10月27日
 * 描述:数据入口
 */
public class DataEngine {

    //默认显示数据
    public static List<TaskModel.returndata> loadInitDatas() {
        List<TaskModel.returndata> datas = new ArrayList<>();
        //for (int i = 0; i < 20; i++) {
            datas.add(new TaskModel.returndata("2015年综合素质测评", "各班班主任组好综合素质测评的各项工作大肆发放的法人","2016-3-17","周晨希",R.drawable.profile,1));
        //}
        return datas;
    }

    //读取上拉刷新数据
    public static List<TaskModel.returndata> loadNewData() {
        List<TaskModel.returndata> datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            datas.add(new TaskModel.returndata("第二学期平困生认定" , "班长组织班上评议小组家斯蒂芬斯达克等你阿桑多瓦的委屈" ,"发布时间：2016-3-17","来自：周晨希",R.drawable.profile3,1));
        }
        return datas;
    }

    //读取下拉更多数据
    public static List<TaskModel.returndata> loadMoreData() {
        List<TaskModel.returndata> datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            datas.add(new TaskModel.returndata("遇见你东辉撒旦发射" + i, "班长组织班上评议小组家斯蒂芬斯达克等你阿的巍峨伟" + i,"发布时间：2016-3-17","来自：周晨希",R.drawable.profile4,1));
        }
        return datas;
    }

    public static View getCustomHeaderOrFooterView(Context context) {
        List<View> datas = new ArrayList<>();
        datas.add(View.inflate(context, R.layout.view_one, null));
        datas.add(View.inflate(context, R.layout.view_two, null));
        datas.add(View.inflate(context, R.layout.view_three, null));
        datas.add(View.inflate(context, R.layout.view_four, null));

        View customHeaderView = View.inflate(context, R.layout.view_custom_header, null);
        BGABanner banner = (BGABanner) customHeaderView.findViewById(R.id.banner);
        banner.setViewPagerViews(datas);

        return customHeaderView;
    }

    //访问网络子进程消息接口
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.getData().get("result");
            Toast.makeText(MainActivity.mainActivity, "请求结果为：" + result, Toast.LENGTH_SHORT).show();

        }
    };

    class loadTask implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            String result = null;




            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            //设置消息标示
            message.setData(bundle);//消息内容
            handler.sendMessage(message);//发送消息
            Looper.loop();
        }

    }



}