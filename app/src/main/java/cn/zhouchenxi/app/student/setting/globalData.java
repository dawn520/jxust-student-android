package cn.zhouchenxi.app.student.setting;

import java.util.Random;

import cn.zhouchenxi.app.student.R;

/**
 * Created by xixi on 16/4/16.
 */
public class globalData {

    //服务器地址
    public static String siteUrl ="http://192.168.0.104/jxust-student/";

    //用户ID
    public static String uid;

    //用户身份验证码
    public static String varifyCode;

    //用户姓名
    public static String userName;

    //cid是否验证失败
    public static int cidFailed =0;

    //随机获取任务图片
    public static int getRandomCheeseDrawable() {
        final Random RANDOM = new Random();
        switch (RANDOM.nextInt(5)) {
            default:
            case 0:
                return R.drawable.cheese_1;
            case 1:
                return R.drawable.cheese_2;
            case 2:
                return R.drawable.cheese_3;
            case 3:
                return R.drawable.cheese_4;
            case 4:
                return R.drawable.cheese_5;
        }
    }

}
