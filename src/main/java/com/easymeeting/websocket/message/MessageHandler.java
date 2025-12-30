package com.easymeeting.websocket.message;

import com.easymeeting.entity.dto.MessageSendDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component("messageHandler")
public interface MessageHandler {
    void listenMessage();

    void sendMessage(MessageSendDto messageSendDto) throws IOException, TimeoutException;

}
