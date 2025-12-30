package com.easymeeting.websocket.netty;

import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.MessageSendDto;
import com.easymeeting.entity.dto.PeerConnectionDataDto;
import com.easymeeting.entity.dto.PeerMessageDto;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.MessageSend2TypeEnum;
import com.easymeeting.entity.enums.MessageTypeEnum;
import com.easymeeting.entity.po.UserInfo;
import com.easymeeting.entity.query.UserInfoQuery;
import com.easymeeting.mappers.UserInfoMapper;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.utils.JsonUtils;
import com.easymeeting.websocket.message.MessageHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@ChannelHandler.Sharable
@Slf4j
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Autowired
    private RedisComponent redisComponent;
    @Autowired
    private MessageHandler messageHandler;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有人进入链接.....");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        log.info("有人退出链接.....");
        Attribute<String> attribute = ctx.channel().attr(AttributeKey.valueOf(ctx.channel().id().toString()));
        String userId = attribute.get();
        UserInfo userInfo = new UserInfo();
        userInfo.setLastOffTime(System.currentTimeMillis());
        userInfoMapper.updateByUserId(userInfo,userId);
    }

    //这段代码处理WebSocket文本消息 发送peerconnection信息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame){
        String text = textWebSocketFrame.text();
        if (Constants.PING.equals(text)) return;

        log.error("收到消息:{}",text);

        PeerConnectionDataDto dataDto = JsonUtils.convertJson2Obj(text, PeerConnectionDataDto.class);
        TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto(dataDto.getToken());
        if (tokenUserInfoDto == null) return;

        PeerMessageDto peerMessageDto = new PeerMessageDto();
        peerMessageDto.setSignalData(dataDto.getSignalData());
        peerMessageDto.setSignalType(dataDto.getSignalType());

        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.PEER.getType());
        messageSendDto.setMessageContent(peerMessageDto);
        messageSendDto.setMeetingId(tokenUserInfoDto.getCurrentMeetingId());
        messageSendDto.setSendUserId(tokenUserInfoDto.getUserId());
        messageSendDto.setReceiveUserId(dataDto.getReceiveUserId());
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());

        messageHandler.sendMessage(messageSendDto);
    }
}