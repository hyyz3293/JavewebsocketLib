package com.jack.websocket.response;

import com.jack.websocket.dispatcher.IResponseDispatcher;
import com.jack.websocket.dispatcher.ResponseDelivery;

/**
 * WebSocket 响应数据接口
 * Created by jack on 2020/02/25
 */
public interface Response<T> {

    /**
     * 获取响应的数据
     */
    T getResponseData();

    /**
     * 设置响应的数据
     */
    void setResponseData(T responseData);

    void onResponse(IResponseDispatcher dispatcher, ResponseDelivery delivery);

    /**
     * 回收资源
     */
    void release();
}
