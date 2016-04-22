package cn.zhouchenxi.app.student.method;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.HashMap;
import java.util.Map;

import cn.zhouchenxi.app.student.LoginActivity;

/**
 * Created by xixi on 16/4/15.
 */
public class mapRead {


    /**
     * 将数据存储/修改进入共享参数
     */
    public static boolean saveMsg(String fileName, Map<String, Object> map,Context context) {
        boolean flag = false;
        // 一般Mode都使用private,比较安全
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Map类提供了一个称为entrySet()的方法，这个方法返回一个Map.Entry实例化后的对象集。
        // 接着，Map.Entry类提供了一个getKey()方法和一个getValue()方法，
        // 因此，上面的代码可以被组织得更符合逻辑
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object object = entry.getValue();
            // 根据值得不同类型，添加
            if (object instanceof Boolean) {
                Boolean new_name = (Boolean) object;
                editor.putBoolean(key, new_name);
            } else if (object instanceof Integer) {
                Integer integer = (Integer) object;
                editor.putInt(key, integer);
            } else if (object instanceof Float) {
                Float f = (Float) object;
                editor.putFloat(key, f);
            } else if (object instanceof Long) {
                Long l = (Long) object;
                editor.putLong(key, l);
            } else if (object instanceof String) {
                String s = (String) object;
                editor.putString(key, s);
            }
        }
        flag = editor.commit();
        return flag;

    }

    /**
     *读取SharedPreferences的数据并返回为map
     * @param fileName SharedPreferences的name
     * @param context  上下文
     * @return
     */
    public static Map<String, ?> getMsg(String fileName,Context context) {
        Map<String, ?> map;
        // 读取数据用不到edit
        SharedPreferences preferences = context.getSharedPreferences(fileName,
                Context.MODE_APPEND);
        //Context.MODE_APPEND可以对已存在的值进行修改
        map = preferences.getAll();
        return map;
    }


}
