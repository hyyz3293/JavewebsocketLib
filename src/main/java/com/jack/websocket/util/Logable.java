package com.jack.websocket.util;

import android.util.Log;

/**
 * 打印日志接口
 * <p>
 * Created by jack on 2020/02/25
 */
public interface Logable {

    void v(String tag, String msg);

    void v(String tag, String msg, Throwable tr);

    void d(String tag, String text);

    void d(String tag, String text, Throwable tr);

    void i(String tag, String text);

    void i(String tag, String text, Throwable tr);

    void e(String tag, String text);

    void e(String tag, String msg, Throwable tr);

    void w(String tag, Throwable tr);

    void wtf(String tag, String msg);

    void wtf(String tag, Throwable tr);

    void wtf(String tag, String msg, Throwable tr);
}
