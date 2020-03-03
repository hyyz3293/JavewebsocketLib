package com.jack.websocket.response;

import android.text.TextUtils;

import com.jack.websocket.dispatcher.IResponseDispatcher;
import com.jack.websocket.dispatcher.ResponseDelivery;

/**
 * 默认的消息响应事件包装类，
 * 只包含文本，不包含数据实体
 * Created by jack on 2020/02/25
 */
public class TextResponse implements Response<String> {

    private String responseText;

    TextResponse() {
    }

    @Override
    public String getResponseData() {
        return responseText;
    }

    @Override
    public void setResponseData(String responseData) {
        this.responseText = responseData;
    }

    @Override
    public void onResponse(IResponseDispatcher dispatcher, ResponseDelivery delivery) {
        dispatcher.onMessage(responseText, delivery);
        release();
    }

    @Override
    public String toString() {
        return String.format("[@TextResponse%s->responseText:%s]",
                hashCode(),
                TextUtils.isEmpty(responseText) ?
                        "null" :
                        responseText);
    }

    @Override
    public void release() {
        ResponseFactory.releaseTextResponse(this);
    }
}
