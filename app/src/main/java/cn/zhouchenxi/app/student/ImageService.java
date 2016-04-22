package cn.zhouchenxi.app.student;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhouchenxi on 2015/10/19.
 * 获取一张图片的类
 */

public class ImageService {

    public static Bitmap getImage(String path) throws IOException {
        URL url = new URL(path);
        Bitmap bitmap = null;
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");   //设置请求方法为GET
        conn.setReadTimeout(5 * 1000);    //设置请求过时时间为5秒
        conn.setDoInput(true);   //打开输入流
        int responseCode = conn.getResponseCode();   // 获取服务器响应值
        InputStream inputStream;
        if (responseCode == HttpURLConnection.HTTP_OK) {        //正常连接
            inputStream = conn.getInputStream();   //通过输入流获得图片数据
            bitmap = BitmapFactory.decodeStream(inputStream);
        }
        return bitmap;

    }
}