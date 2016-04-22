package cn.zhouchenxi.app.student.getui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.zhouchenxi.app.student.LoginActivity;
import cn.zhouchenxi.app.student.MainActivity;
import cn.zhouchenxi.app.student.method.connectServer;
import cn.zhouchenxi.app.student.method.mapRead;
import cn.zhouchenxi.app.student.setting.globalData;

public class PushDemoReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    public String siteurl =globalData.siteUrl;
    public  Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));
                Log.e("zcx", "zcx");

                if(result){
                    Intent intentTemp = new Intent(context.getApplicationContext(),MainActivity.class);
                    intentTemp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    Bundle bundledata = new Bundle();
                    context.getApplicationContext().startActivity(intentTemp);
                    Log.e("zcx2", "zcx2");
                }

                if (payload != null) {
                    String data = new String(payload);

                    Log.e("zcx3", "zcx3");

                    payloadData.append(data);
                    payloadData.append("\n");

                 //   if (GetuiSdkDemoActivity.tLogView != null) {
                 //       GetuiSdkDemoActivity.tLogView.append(data + "\n");
                 //   }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                String mycid;
                //从共享参数获取数据
                HashMap<String, Object> map = (HashMap<String, Object>) mapRead.getMsg("login", mContext);
                    if (map.get("cid") != null) {
                        Log.e("+++++++", "+++++++");
                        mycid = (String) map.get("cid");
                        if(!mycid.equals(cid)) {
                            HashMap<String, Object> tomap = new HashMap<String, Object>();
                            tomap.put("cid", cid);
                            mapRead.saveMsg("login", tomap, context);
                            Toast.makeText(mContext.getApplicationContext(), "应用中已保存的CID和当前个推生成的CID不一致,请求服务器更新CID", Toast.LENGTH_SHORT).show();
                            Log.e("xxxxxx", "xxxxx");
                            Thread saveCidThread = new Thread(new saveCidHandler());
                            saveCidThread.start();
                        }
                        Log.e("--------", "--------");

                    }else{
                        HashMap<String, Object> tomap = new HashMap<String, Object>();
                        tomap.put("cid", cid);
                        mapRead.saveMsg("login", tomap, mContext);
                        Toast.makeText(mContext.getApplicationContext(), "应用中无已保存的CID,请求服务器存储CID", Toast.LENGTH_SHORT).show();
                        Thread saveCidThread = new Thread(new saveCidHandler());
                        saveCidThread.start();
                    }




                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 * 
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }

    //上传CID到服务器
    public String saveCid() throws JSONException {
        HashMap<String, Object> map = (HashMap<String, Object>) mapRead.getMsg("login", mContext);
        String uid = (String) map.get("uid");
        String varifyCode = (String) map.get("varifycode");
        String cid = (String) map.get("cid");
        HashMap tomap1 = new HashMap<>();
        tomap1.put("uid", uid);
        tomap1.put("varifycode", varifyCode);
        tomap1.put("cid", cid);
        HashMap remap;
        connectServer connect = new connectServer();
        remap =  connect.getData(siteurl+"/index.php/Home/Client/saveCid",tomap1);
        String returnmessage;
        if(Integer.parseInt(remap.get("state").toString())==1){
            String result = remap.get("data").toString();
            JSONObject jsonObject = new JSONObject(result);
            int returnstate = jsonObject.getInt("returnstate");
            if (returnstate == 0) {
                returnmessage = "存储CID时认证失败,请重新登录";
                globalData.cidFailed=1;
                //转到登录界面
                Intent intent = new Intent(mContext.getApplicationContext(), LoginActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(intent);
                MainActivity.mainActivity.finish();
            } else {
                returnmessage = "认证成功,成功存储CID到服务器";

            }
        } else {
            returnmessage = "存储cid时连接服务器失败";
        }
        return returnmessage;

    }

    //访问网络子进程消息接口
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.getData().get("result");
            Toast.makeText(mContext, "请求结果为：" + result, Toast.LENGTH_SHORT).show();

        }
    };

    class saveCidHandler implements Runnable {
        @Override
        public void run() {
            Looper.prepare();
            String result = null;
            Log.e("ceshi", "ceshi");
            try {
                result = saveCid();
            } catch (JSONException e) {
                e.printStackTrace();
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


}
