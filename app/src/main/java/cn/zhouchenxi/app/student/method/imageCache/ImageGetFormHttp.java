package cn.zhouchenxi.app.student.method.imageCache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xixi on 16/4/17.
 */
public class ImageGetFormHttp {

    public static Bitmap downloadBitmap(String path) throws IOException {
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
            inputStream.close();

        }
        return bitmap;
    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
