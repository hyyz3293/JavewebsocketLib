package com.jack.websocket.util;

import com.google.gson.Gson;
import com.jack.websocket.response.MessageEntity;

public class SocketUtils {

    public static String getSendMessage(String opcode) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.opcode = opcode;
        Gson gson = new Gson();
        String heart = gson.toJson(messageEntity);
        return heart;
    }

}
