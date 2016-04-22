package cn.zhouchenxi.app.student;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.zhouchenxi.app.student.method.DeviceUuidFactory;
import cn.zhouchenxi.app.student.method.connectServer;
import cn.zhouchenxi.app.student.method.mapRead;
import cn.zhouchenxi.app.student.method.md5;
import cn.zhouchenxi.app.student.setting.globalData;


public class LoginActivity extends AppCompatActivity {
    public String siteurl = globalData.siteUrl;
    private String account;
    private String password;
    private EditText view_account, view_password;
    private MaterialDialog connectdialog;
    private HashMap<String, Object> map;

    //访问网络子进程消息接口
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.getData().get("result");
            Toast.makeText(LoginActivity.this, "请求结果为：" + result, Toast.LENGTH_SHORT).show();

        }
    };

    class WebServiceHandler implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            String result;
            Log.e("ceshi", "ceshi");
            try {
                result = loginconnect();
            } catch (JSONException e) {
                e.printStackTrace();
                result = "发生异常";
            }
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("result", result);
            //设置消息标示
            message.obj = "webconnect";
            message.setData(bundle);//消息内容
            handler.sendMessage(message);//发送消息
            Looper.loop();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从共享参数获取数据
        map = (HashMap<String, Object>) mapRead.getMsg("login",this);
        if ((map != null && !map.isEmpty())&&globalData.cidFailed==0) {
            if (map.get("user_account") != null) {
                account = (String) map.get("user_account");
                password = (String) map.get("user_password");
                //若值为true,用户无需输入密码，直接跳转
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
        if (globalData.cidFailed==1) {
            new MaterialDialog.Builder(this)
                    .title("系统提示")
                    .content("存储CID时认证失败,请重新登录")
                    .negativeText("确定")
                    .show();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_login);
        view_account = (EditText) findViewById(R.id.account);
        view_password = (EditText) findViewById(R.id.password);
        Button view_loginbutton = (Button) findViewById(R.id.loginbutton);
        view_loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empty_validate()) {
                    connectdialog = new MaterialDialog.Builder(LoginActivity.this)
                            .title("登录")
                            .content("正在连接服务器，请稍后……")
                            .progress(true, 0)
                            .canceledOnTouchOutside(false)
                            .show();

                    Thread accessWebServiceThread = new Thread(new WebServiceHandler());
                    accessWebServiceThread.start();


                }

            }
        });

    }

    public boolean empty_validate() {
        account = view_account.getText().toString();
        password = view_password.getText().toString();
        if (account.equals("")) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.equals("")) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String loginconnect() throws JSONException {
        String udid = md5.stringToMd5(md5.stringToMd5(password) +
                md5.stringToMd5(DeviceUuidFactory.getUDID(this))+account);
        HashMap tomap = new HashMap<>();
        tomap.put("account", account);
        tomap.put("password", md5.stringToMd5(password));
        tomap.put("udid",udid);
        HashMap remap = null;
        connectServer connect = new connectServer();
        try{
            remap =  connect.getData(siteurl+"/index.php/Home/Client",tomap);
        }catch (Exception e){
            e.printStackTrace();
            remap.put("state",0);
        }
        String returnmessage;
        if(Integer.parseInt(remap.get("state").toString())==1){
            connectdialog.dismiss();// 关闭ProgressDialog
            String result = remap.get("data").toString();
            JSONObject jsonObject = new JSONObject(result);
            int returnstate = jsonObject.getInt("returnstate");
            returnmessage = jsonObject.getString("returnmessage");
            if (returnstate == 0) {
                showError(returnmessage);
            }else{
                JSONObject jsonObject1 = jsonObject.getJSONObject("returndata");
                String uid = jsonObject1.getString("uid");
                String user_account = jsonObject1.getString("user_account");
                String user_name = jsonObject1.getString("user_name");
                String group_id = jsonObject1.getString("group_id");
                String varifyCode = jsonObject1.getString("varifycode");
                //登陆成功后将登录的信息存储到本地
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("uid", uid);
                map.put("user_account", user_account);
                map.put("varifycode", varifyCode);
                map.put("user_name", user_name);
                map.put("group_id", group_id);
                mapRead.saveMsg("login", map,this);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            returnmessage = "连接服务器超时，请检查网络";
            connectdialog.setContent("连接服务器超时，请检查网络");
            connectdialog.setActionButton(DialogAction.NEGATIVE, "确定");
        }
        return returnmessage;
    }

    public void showError(String message) {

        new MaterialDialog.Builder(this)
                .title("登录")
                .content(message)
                .negativeText("取消")
                .show();

    }



}
