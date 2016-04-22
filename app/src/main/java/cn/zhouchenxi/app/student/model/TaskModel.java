package cn.zhouchenxi.app.student.model;

import java.util.List;

/**
 * 任务数据模型
 * returnstate   服务器返回状态
 * returnmessage 服务器返回信息
 * returndata    服务器返回数据
 * Created by xixi on 16/4/22.
 */

public class TaskModel {

    public String returnstate;
    public String returnmessage;
    public List<returndata> returndata;

    public static class returndata {
        public String title;
        public String detail;
        public String time;
        public String author;
        public int tid;
        public int fromuid;

        public returndata(String mtitle, String mdetail,String mtime,String mauthor,int mtid,int mfromuid) {
            title = mtitle;
            detail = mdetail;
            time = mtime;
            author = mauthor;
            tid = mtid;
            fromuid = mfromuid;
        }
    }




}