package cn.zhouchenxi.app.student.rfInterface;

import java.util.List;

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

    //读取任务
    @FormUrlEncoded
    @POST("index.php/Home/Client/loadtask")
    Observable<TaskModel> loadTask(
            @Field("uid") int uid,
            @Field("varifycode") String varifyCode,
            @Field("mode") int mode,
            @Field("type") int type
            );

}
