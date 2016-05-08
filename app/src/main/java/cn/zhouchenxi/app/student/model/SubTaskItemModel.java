package cn.zhouchenxi.app.student.model;

/**
 * 任务数据模型
 * returnstate   服务器返回状态
 * returnmessage 服务器返回信息
 * returndata    服务器返回数据
 * Created by xixi on 16/4/27.
 */

public class SubTaskItemModel {

    public String returnstate;
    public String returnmessage;
    public returndata returndata;

    public static class returndata {
        public String title;
        public String time;
        public String author;
        public int tid;
        public int fromuid;
        public String content;

    }




}