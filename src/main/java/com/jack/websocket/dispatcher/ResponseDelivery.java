package com.jack.websocket.dispatcher;

import com.jack.websocket.SocketListener;

/**
 * 消息发射器接口
 *
 * Created by jack on 2020/02/25
 */
public interface ResponseDelivery extends SocketListener {

    void addListener(SocketListener listener);

    void removeListener(SocketListener listener);

    void clear();

    boolean isEmpty();
}
