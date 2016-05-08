package cn.zhouchenxi.app.student.rfInterface;

import java.util.List;

import cn.zhouchenxi.app.student.model.PostCommentModel;
import cn.zhouchenxi.app.student.model.SubTaskItemModel;
import cn.zhouchenxi.app.student.model.TaskCommentModel;
import cn.zhouchenxi.app.student.model.TaskItemModel;
import cn.zhouchenxi.app.student.model.TaskModel;
import cn.zhouchenxi.app.student.model.TestModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * retrofit2连接服务器接口
 * Created by xixi on 16/4/22.
 */
public interface WebService {

    @GET("index.php/Home/Client/{id}")
    Call<List<TestModel>> listRepos(@Path("id") int id);

    //读取任务列表
    @FormUrlEncoded
    @POST("index.php/Home/Client/loadtask")
    Observable<TaskModel> loadTask(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("mode") int mode,
            @Field("type") int type,
            @Field("flag") int flag
            );
    //读取任务详情
    @FormUrlEncoded
    @POST("index.php/Home/Client/taskItem")
    Observable<TaskItemModel> taskItem(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("tid") int tid
    );
    //读取任务评论
    @FormUrlEncoded
    @POST("index.php/Home/Client/loadTaskComment")
    Observable<TaskCommentModel> loadTaskComment(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("tid") int tid,
            @Field("mode") int mode,
            @Field("flag") int flag

    );
    //发送任务评论
    @FormUrlEncoded
    @POST("index.php/Home/Client/postTaskComment")
    Observable<PostCommentModel> postTaskComment(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("tid") int tid,
            @Field("replyid") int replyId,
            @Field("touid") int toUid,
            @Field("content") String content
    );
    //接受任务
    @FormUrlEncoded
    @POST("index.php/Home/Client/jsTask")
    Observable<PostCommentModel> jsTask(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("tid") int tid
    );
    //查看提交的任务
    @FormUrlEncoded
    @POST("index.php/Home/Client/subTaskItem")
    Observable<SubTaskItemModel> subTaskItem(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("subid") int subId
    );
    //读取提交的任务的评论
    @FormUrlEncoded
    @POST("index.php/Home/Client/loadSubTaskComment")
    Observable<TaskCommentModel> loadSubTaskComment(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("tid") int tid,
            @Field("mode") int mode,
            @Field("flag") int flag
    );
    //发送任务评论
    @FormUrlEncoded
    @POST("index.php/Home/Client/postSubTaskComment")
    Observable<PostCommentModel> postSubTaskComment(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("tid") int tid,
            @Field("replyid") int replyId,
            @Field("touid") int toUid,
            @Field("content") String content,
            @Field("ttid") int ttid
    );

}
