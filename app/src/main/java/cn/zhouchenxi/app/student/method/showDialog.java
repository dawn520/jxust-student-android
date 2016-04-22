package cn.zhouchenxi.app.student.method;

import android.content.Context;
import android.content.DialogInterface;

import com.afollestad.materialdialogs.AlertDialogWrapper;

/**
 * Created by xixi on 16/4/15.
 */
public class showDialog {

    public void showError(Context context) {

        new AlertDialogWrapper.Builder(context)
                .setTitle("登录")
                .setMessage("用户名或密码错误")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }

}
