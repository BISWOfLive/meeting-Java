package com.easymeeting.controller;


import com.easymeeting.annotition.GlobalInterceptor;
import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.TokenUserInfoDto;
import com.easymeeting.entity.enums.ReceiveTypeEnum;
import com.easymeeting.entity.enums.ResponseCodeEnum;
import com.easymeeting.entity.po.MeetingChatMessage;
import com.easymeeting.entity.query.MeetingChatMessageQuery;
import com.easymeeting.entity.query.MeetingMemberQuery;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.vo.ResponseVO;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.service.MeetingChatMessageService;
import com.easymeeting.service.MeetingMemberService;
import com.easymeeting.utils.TableSplitUtils;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/chat")
@Validated
@Slf4j
public class ChatController extends ABaseController {

    @Resource
    private MeetingChatMessageService meetingChatMessageService;

    @Resource
    private MeetingMemberService meetingMemberService;

    @RequestMapping("/loadMessage")
    @GlobalInterceptor
    public ResponseVO loadMessage(Long maxMessageId, Integer pageNo) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        String meetingId = tokenUserInfo.getCurrentMeetingId();
        MeetingChatMessageQuery param = new MeetingChatMessageQuery();
        param.setMeetingId(meetingId);
        param.setUserId(tokenUserInfo.getUserId());
        param.setMaxMessageId(maxMessageId);
        param.setPageNo(pageNo);
        param.setOrderBy("m.message_id desc");
        String tableName = TableSplitUtils.getMeetingChatMessageTable(meetingId);
        PaginationResultVO<MeetingChatMessage> resultVO = meetingChatMessageService.findListByPage(tableName, param);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVO sendMessage(String message,
                                  @NotNull Integer messageType,
                                  @NotEmpty String receiveUserId,
                                  String fileName,
                                  Long fileSize,
                                  Integer fileType) {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        MeetingChatMessage chatMessage = new MeetingChatMessage();
        chatMessage.setMessageType(messageType);
        chatMessage.setMessageContent(message);
        chatMessage.setFileName(fileName);
        chatMessage.setFileSize(fileSize);
        chatMessage.setFileType(fileType);
        chatMessage.setSendUserId(tokenUserInfo.getUserId());
        chatMessage.setSendUserNickName(tokenUserInfo.getNickName());
        chatMessage.setMeetingId(tokenUserInfo.getCurrentMeetingId());
        if (Constants.ZERO_STR.equals(receiveUserId)) {
            chatMessage.setReceiveType(ReceiveTypeEnum.ALL.getType());
        } else {
            chatMessage.setReceiveType(ReceiveTypeEnum.USER.getType());
        }
        chatMessage.setReceiveUserId(receiveUserId);
        meetingChatMessageService.saveChatMessage(chatMessage);
        return getSuccessResponseVO(chatMessage);
    }

    @RequestMapping("/uploadFile")
    @GlobalInterceptor
    public ResponseVO uploadFile(@NotNull MultipartFile file,
                                 @NotNull Long messageId,
                                 @NotNull Long sendTime) throws IOException {
        TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
        meetingChatMessageService.uploadFile(file, tokenUserInfo.getCurrentMeetingId(), messageId, sendTime);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadHistoryMessage")
    @GlobalInterceptor
    public ResponseVO loadHistoryMessage(String meetingId, Long maxMessageId, Integer pageNo) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        checkMember(meetingId, tokenUserInfoDto.getUserId());
        MeetingChatMessageQuery param = new MeetingChatMessageQuery();
        param.setMeetingId(meetingId);
        param.setUserId(tokenUserInfoDto.getUserId());
        param.setMaxMessageId(maxMessageId);
        param.setPageNo(pageNo);
        param.setOrderBy("m.message_id desc");
        String tableName = TableSplitUtils.getMeetingChatMessageTable(meetingId);
        PaginationResultVO resultvo = meetingChatMessageService.findListByPage(tableName, param);
        return getSuccessResponseVO(resultvo);
    }

    private void checkMember(String meetingId, String userId) {
        MeetingMemberQuery meetingMemberQuery = new MeetingMemberQuery();
        meetingMemberQuery.setMeetingId(meetingId);
        meetingMemberQuery.setUserId(userId);
        Integer count = meetingMemberService.findCountByParam(meetingMemberQuery);
        if (count == 0){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
