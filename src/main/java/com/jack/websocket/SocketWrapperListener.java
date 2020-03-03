package com.jack.websocket;

import com.jack.websocket.request.Request;
import com.jack.websocket.response.Response;

/**
 * {@link WebSocketWrapper} 监听器
 * <p>
 * Created by jack on 2020/02/25
 */
public interface SocketWrapperListener {

    /**
     * 连接成功
     */
    void onConnected();

    /**
     * 连接失败
     */
    void onConnectFailed(Throwable e);

    /**
     * 连接断开
     */
    void onDisconnect();

    /**
     * 数据发送失败
     *
     * @param request 发送的请求
     * @param type    失败类型：{@link com.jack.websocket.response.ErrorResponse#ERROR_NO_CONNECT} 未连接、
     *                {@link com.jack.websocket.response.ErrorResponse#ERROR_UNKNOWN} 未知错误、
     *                {@link com.jack.websocket.response.ErrorResponse#ERROR_UN_INIT} 初始化未完成
     */
    void onSendDataError(Request request, int type, Throwable tr);

    /**
     * 接收到消息
     */
    void onMessage(Response message);
}
