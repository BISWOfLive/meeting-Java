package com.easymeeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MessageSendDto<T> implements Serializable {
        private static final long serialVersionUID = -1045752033171142417L;

        private Integer messageSend2Type;

        private String meetingId;
        //消息类型
        private Integer messageType;

        private String sendUserId;

        private String sendUserNickName;

        private T messageContent;

        private String receiveUserId;

        private long sendTime;

        private String messageId;

        private Integer status;

        private String fileName;

        private Integer fileType;

        private long fileSize;

        public Integer getMessageSend2Type() {
            return messageSend2Type;
        }

        public void setMessageSend2Type(Integer messageSend2Type) {
            this.messageSend2Type = messageSend2Type;
        }

        public String getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public Integer getMessageType() {
            return messageType;
        }

        public void setMessageType(Integer messageType) {
            this.messageType = messageType;
        }

        public String getSendUserId() {
            return sendUserId;
        }

        public void setSendUserId(String sendUserId) {
            this.sendUserId = sendUserId;
        }

        public String getSendUserNickName() {
            return sendUserNickName;
        }

        public void setSendUserNickName(String sendUserNickName) {
            this.sendUserNickName = sendUserNickName;
        }

        public T getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(T messageContent) {
            this.messageContent = messageContent;
        }

        public String getReceiveUserId() {
            return receiveUserId;
        }

        public void setReceiveUserId(String receiveUserId) {
            this.receiveUserId = receiveUserId;
        }

        public long getSendTime() {
            return sendTime;
        }

        public void setSendTime(long sendTime) {
            this.sendTime = sendTime;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getFileType() {
            return fileType;
        }

        public void setFileType(Integer fileType) {
            this.fileType = fileType;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
    }
