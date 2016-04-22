package cn.zhouchenxi.app.student.model;

import java.util.List;

/**
 * 公共数据模型
 * returnstate   服务器返回状态
 * returnmessage 服务器返回信息
 * returndata    服务器返回数据
 * Created by xixi on 16/4/22.
 */

public class PublicModel {

    public String returnstate;
    public String returnmessage;
    public List<TaskDate> returndata;

    public static class TaskDate {
        public String title;
        public String detail;
        public String time;
        public String author;
        public int tid;
        public int fromuid;

    }



}