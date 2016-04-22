package cn.zhouchenxi.app.student.setting;

/**
 * Created by xixi on 16/4/16.
 */
public class globalData {

    //服务器地址
    public static String siteUrl ="http://192.168.1.155/jxust-student/";

    //cid是否验证失败
    public static int cidFailed =0;

    public static int getCidFailed() {
        return cidFailed;
    }

    public static void setCidFailed(int cidFailed) {
        globalData.cidFailed = cidFailed;
    }

}
