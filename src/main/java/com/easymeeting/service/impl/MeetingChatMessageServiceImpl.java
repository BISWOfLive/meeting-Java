package com.easymeeting.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.easymeeting.entity.config.AppConfig;
import com.easymeeting.entity.constants.Constants;
import com.easymeeting.entity.dto.MessageSendDto;
import com.easymeeting.entity.enums.*;
import com.easymeeting.exception.BusinessException;
import com.easymeeting.utils.*;
import com.easymeeting.websocket.message.MessageHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easymeeting.entity.query.MeetingChatMessageQuery;
import com.easymeeting.entity.po.MeetingChatMessage;
import com.easymeeting.entity.vo.PaginationResultVO;
import com.easymeeting.entity.query.SimplePage;
import com.easymeeting.mappers.MeetingChatMessageMapper;
import com.easymeeting.service.MeetingChatMessageService;
import org.springframework.web.multipart.MultipartFile;


/**
 * 会议聊天消息表 业务接口实现
 */
@Service("meetingChatMessageService")
public class MeetingChatMessageServiceImpl implements MeetingChatMessageService {

    @Resource
    private MeetingChatMessageMapper<MeetingChatMessage, MeetingChatMessageQuery> meetingChatMessageMapper;

    @Resource
    private MessageHandler messageHandler;

    @Resource
    private AppConfig appConfig;

    @Resource
    private FFmpegUtils fFmpegUtils;
    /**
     * 根据条件查询列表
     */
    @Override
    public List<MeetingChatMessage> findListByParam(String tableName, MeetingChatMessageQuery param) {
        return this.meetingChatMessageMapper.selectList(tableName, param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(String tableName, MeetingChatMessageQuery param) {
        return this.meetingChatMessageMapper.selectCount(tableName, param);
    }

    /**
     * 分页查询方法
     */
    @Override
    public PaginationResultVO<MeetingChatMessage> findListByPage(String tableName, MeetingChatMessageQuery param) {
        int count = this.findCountByParam(tableName, param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<MeetingChatMessage> list = this.findListByParam(tableName, param);
        PaginationResultVO<MeetingChatMessage> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增
     */
    @Override
    public Integer add(String tableName, MeetingChatMessage bean) {
        return this.meetingChatMessageMapper.insert(tableName, bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(String tableName, List<MeetingChatMessage> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.meetingChatMessageMapper.insertBatch(tableName, listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(String tableName, List<MeetingChatMessage> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.meetingChatMessageMapper.insertOrUpdateBatch(tableName, listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(String tableName, MeetingChatMessage bean, MeetingChatMessageQuery param) {
        StringTools.checkParam(param);
        return this.meetingChatMessageMapper.updateByParam(tableName, bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(String tableName, MeetingChatMessageQuery param) {
        StringTools.checkParam(param);
        return this.meetingChatMessageMapper.deleteByParam(tableName, param);
    }

    /**
     * 根据MessageId获取对象
     */
    @Override
    public MeetingChatMessage getMeetingChatMessageByMessageId(String tableName, Long messageId) {
        return this.meetingChatMessageMapper.selectByMessageId(tableName, messageId);
    }

    /**
     * 根据MessageId修改
     */
    @Override
    public Integer updateMeetingChatMessageByMessageId(String tableName, MeetingChatMessage bean, Long messageId) {
        return this.meetingChatMessageMapper.updateByMessageId(tableName, bean, messageId);
    }

    /**
     * 根据MessageId删除
     */
    @Override
    public Integer deleteMeetingChatMessageByMessageId(String tableName, Long messageId) {
        return this.meetingChatMessageMapper.deleteByMessageId(tableName, messageId);
    }

    @Override
    public void saveChatMessage(MeetingChatMessage chatMessage) {
        if (!ArrayUtils.contains(new Integer[]{MessageTypeEnum.CHAT_TEXT_MESSAGE.getType(), MessageTypeEnum.CHAT_MEDIA_MESSAGE.getType()},
                chatMessage.getMessageType())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        ReceiveTypeEnum receiveTypeEnum = ReceiveTypeEnum.getByType(chatMessage.getReceiveType());
        if (null == receiveTypeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if(receiveTypeEnum == ReceiveTypeEnum.USER && StringTools.isEmpty(chatMessage.getReceiveUserId())){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(chatMessage.getMessageType());
        if (messageTypeEnum == MessageTypeEnum.CHAT_TEXT_MESSAGE) {
            if (StringTools.isEmpty(chatMessage.getMessageContent())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        } else if (messageTypeEnum == MessageTypeEnum.CHAT_MEDIA_MESSAGE) {
            if (StringTools.isEmpty(chatMessage.getFileName()) || chatMessage.getFileSize() == null || chatMessage.getFileType() == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            chatMessage.setFileSuffix(StringTools.getFileSuffix(chatMessage.getFileName()));
            chatMessage.setStatus(MessageStatusEnum.SENDING.getStatus());
        }
        chatMessage.setSendTime(System.currentTimeMillis());
        chatMessage.setMessageId(SnowFlakeUtils.nextId());
        String tableName = TableSplitUtils.getMeetingChatMessageTable(chatMessage.getMeetingId());
        meetingChatMessageMapper.insert(tableName,chatMessage);
        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        if (ReceiveTypeEnum.USER == receiveTypeEnum){
            messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.USER.getType());
            messageHandler.sendMessage(messageSendDto);
            //给自己发一条
            messageSendDto.setReceiveUserId(chatMessage.getSendUserId());
            messageHandler.sendMessage(messageSendDto);
        }else {
            messageSendDto.setMessageSend2Type(MessageSend2TypeEnum.GROUP.getType());
            messageHandler.sendMessage(messageSendDto);
        }
    }

    @Override
    public void uploadFile(MultipartFile file, String meetingId, Long messageId, Long sendTime) throws IOException {
        String month = DateUtil.format(new Date(sendTime), DateTimePatternEnum.YYYYMM.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + month;
        File folderFile = new File(folder);
        if (!folderFile.exists()){
            folderFile.mkdirs();
        }
        String filePath = folder + "/" + messageId;
        String fileName = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(fileName);
        FileTypeEnum fileTypeEnum = FileTypeEnum.getBySuffix(fileSuffix);
        if (FileTypeEnum.IMAGE == fileTypeEnum){
            File tempFile = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + StringTools.getRandomString(Constants.LENGTH_30));
            file.transferTo(tempFile);
            filePath = filePath + Constants.IMAGE_SUFFIX;
            filePath = fFmpegUtils.transferImageType(tempFile, filePath);
            fFmpegUtils.createImageThumbnail(filePath);
        }else if (fileTypeEnum == FileTypeEnum.VIDEO){
            File tempFile = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + StringTools.getRandomString(Constants.LENGTH_30));
            file.transferTo(new File(filePath));
            filePath = filePath + Constants.VIDEO_SUFFIX;
            fFmpegUtils.transferVideoType(tempFile,filePath,fileSuffix);
            fFmpegUtils.createImageThumbnail(filePath);
        }else {
            filePath = filePath + fileSuffix;
            file.transferTo(new File(filePath));
        }
        String tableName = TableSplitUtils.getMeetingChatMessageTable(meetingId);
        MeetingChatMessage chatMessage = new MeetingChatMessage();
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus());
        this.meetingChatMessageMapper.updateByMessageId(tableName,chatMessage,messageId);

        MessageSendDto messageSendDto = new MessageSendDto () ;
        messageSendDto.setMeetingId(meetingId);
        messageSendDto.setMessageType (MessageTypeEnum. CHAT_MEDIA_MESSAGE_UPDATE.getType ()) ;
        messageSendDto.setStatus(MessageStatusEnum.SENDED.getStatus ());
        messageSendDto.setMessageId(messageId) ;
        messageSendDto.setMessageSend2Type(MessageSend2TypeEnum. GROUP.getType ());
        messageHandler.sendMessage(messageSendDto);

    }
}