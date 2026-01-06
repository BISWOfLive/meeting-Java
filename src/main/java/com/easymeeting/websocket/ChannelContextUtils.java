package com.easymeeting.websocket;


import com.alibaba.fastjson.JSON;
import com.easymeeting.entity.dto.MeetingExitDto;
import com.easymeeting.entity.dto.MeetingMemberDto;
import com.easymeeting.entity.dto.MessageSendDto;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.MeetingMemberStatusEnum;
import com.easymeeting.entity.enums.MessageSend2TypeEnum;
import com.easymeeting.entity.enums.MessageTypeEnum;
import com.easymeeting.entity.po.UserInfo;
import com.easymeeting.mappers.UserInfoMapper;
import com.easymeeting.redis.RedisComponent;
import com.easymeeting.utils.JsonUtils;
import com.easymeeting.utils.StringTools;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChannelContextUtils {
    public static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();

    public static final ConcurrentHashMap<String, ChannelGroup> MEETING_ROOM_CONTEXT_MAP = new ConcurrentHashMap<>();

    @Resource
    private final UserInfoMapper userInfoMapper;

    @Resource
    private final RedisComponent redisComponent;


    public ChannelContextUtils(UserInfoMapper userInfoMapper, RedisComponent redisComponent) {
        this.userInfoMapper = userInfoMapper;
        this.redisComponent = redisComponent;
    }

    public void addContext(String userId, Channel channel) {
        try {
            String channelId = channel.id().toString();
            AttributeKey attributeKey = null;
            if (!AttributeKey.exists(channelId)) {
                attributeKey = AttributeKey.newInstance(channelId);
            } else {
                attributeKey = AttributeKey.valueOf(channelId);
            }

            channel.attr(attributeKey).set(userId);
            USER_CONTEXT_MAP.put(userId, channel);

            UserInfo userInfo = new UserInfo();
            userInfo.setLastLoginTime(System.currentTimeMillis());
            userInfoMapper.updateByUserId(userInfo, userId);

            TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDtoByUserId(userId);
            if (tokenUserInfoDto.getCurrentMeetingId() != null) {
                addMeetingRoom(tokenUserInfoDto.getCurrentMeetingId(), userId);
            }
        } catch (Exception e) {
            log.error("初始化连接失败", e);
        }
    }

    public void addMeetingRoom(String meetingId, String userId) {
        Channel context = USER_CONTEXT_MAP.get(userId);
        if (context == null) {
            return;
        }
        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            MEETING_ROOM_CONTEXT_MAP.put(meetingId, group);
        }
        Channel channel = group.find(context.id());
        if (channel == null) {
            group.add(context);
        }
    }

    public void sendMessage(MessageSendDto messageSendDto) {
        if (MessageSend2TypeEnum.USER.getType().equals(messageSendDto.getMessageSend2Type())) {
            sendMsg2User(messageSendDto);
        } else {
            sendMsg2Group(messageSendDto);
        }
    }

    private void sendMsg2Group(MessageSendDto messageSendDto) {
        if (messageSendDto.getMeetingId() == null) return;

        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(messageSendDto.getMeetingId());

        if (group == null) return;

        group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageSendDto)));

        // 为什么不能把这段代码写到exitMeetingRoom这个方法下面
        // 因为用中间件做了消息的订阅与发布，这个操作是异步的
        // 如果直接在业务代码写关闭channel的逻辑可能会导致消息还未发送出去channel就被关闭了
        if (MessageTypeEnum.EXIT_MEETING_ROOM.getType().equals(messageSendDto.getMessageType())){
            MeetingExitDto exitDto = JsonUtils.convertJson2Obj((String) messageSendDto.getMessageContent(), MeetingExitDto.class);
            removeContextFromGroup(exitDto.getExitUserId(),messageSendDto.getMeetingId());
            List<MeetingMemberDto> meetingMemberDtoList = redisComponent.getMeetingMemberList(messageSendDto.getMeetingId());
            List<MeetingMemberDto> onLineMemberList = meetingMemberDtoList.stream().filter(item -> MeetingMemberStatusEnum.NORMAL.getStatus().equals(item.getStatus())).collect(Collectors.toList());
            if (onLineMemberList.isEmpty())  removeContextGroup(messageSendDto.getMeetingId());
            return;
        }
        if (MessageTypeEnum.FINIS_MESSAGE.getType().equals(messageSendDto.getMessageType())){
            List<MeetingMemberDto> meetingMemberDtoList = redisComponent.getMeetingMemberList(messageSendDto.getMeetingId());
            for (MeetingMemberDto meetingMemberDto :meetingMemberDtoList){
                removeContextFromGroup(meetingMemberDto.getUserId(),messageSendDto.getMeetingId());
            }
            removeContextGroup(messageSendDto.getMeetingId());
        }
    }

    private void removeContextGroup(String meetingId){
        MEETING_ROOM_CONTEXT_MAP.remove(meetingId);
    }

    private void sendMsg2User(MessageSendDto messageSendDto) {
        if (messageSendDto.getReceiveUserId() == null) return;

        Channel channel = USER_CONTEXT_MAP.get(messageSendDto.getReceiveUserId());

        if (channel == null) return;

        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageSendDto)));
    }

    private void removeContextFromGroup(String userId,String meetingId){
        Channel context = USER_CONTEXT_MAP.get(userId);
        if (null == context){
            return;
        }

        ChannelGroup group = MEETING_ROOM_CONTEXT_MAP.get(meetingId);
        if (group != null){
            group.remove(context);
        }
    }

    public void closeContext(String userId) {
        if (StringTools.isEmpty(userId)) return;

        Channel channel = USER_CONTEXT_MAP.get(userId);
        USER_CONTEXT_MAP.remove(userId);
        if (channel != null) channel.close();
    }
}

