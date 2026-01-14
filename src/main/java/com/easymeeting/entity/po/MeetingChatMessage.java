package com.easymeeting.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 会议聊天消息表
 */
public class MeetingChatMessage implements Serializable {


	/**
	 * 消息ID
	 */
	private Long messageId;

	/**
	 * 会议ID
	 */
	private String meetingId;

	/**
	 * 消息类型
	 */
	private Integer messageType;

	/**
	 * 消息内容
	 */
	private String messageContent;

	/**
	 * 发送用户ID
	 */
	private String sendUserId;

	/**
	 * 发送用户昵称
	 */
	private String sendUserNickName;

	/**
	 * 发送时间
	 */
	private Long sendTime;

	/**
	 * 接收类型
	 */
	private Integer receiveType;

	/**
	 * 接收用户ID
	 */
	private String receiveUserId;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件类型
	 */
	private Integer fileType;

	/**
	 * 文件后缀
	 */
	private String fileSuffix;

	/**
	 * 状态
	 */
	private Integer status;


	public void setMessageId(Long messageId){
		this.messageId = messageId;
	}

	public Long getMessageId(){
		return this.messageId;
	}

	public void setMeetingId(String meetingId){
		this.meetingId = meetingId;
	}

	public String getMeetingId(){
		return this.meetingId;
	}

	public void setMessageType(Integer messageType){
		this.messageType = messageType;
	}

	public Integer getMessageType(){
		return this.messageType;
	}

	public void setMessageContent(String messageContent){
		this.messageContent = messageContent;
	}

	public String getMessageContent(){
		return this.messageContent;
	}

	public void setSendUserId(String sendUserId){
		this.sendUserId = sendUserId;
	}

	public String getSendUserId(){
		return this.sendUserId;
	}

	public void setSendUserNickName(String sendUserNickName){
		this.sendUserNickName = sendUserNickName;
	}

	public String getSendUserNickName(){
		return this.sendUserNickName;
	}

	public void setSendTime(Long sendTime){
		this.sendTime = sendTime;
	}

	public Long getSendTime(){
		return this.sendTime;
	}

	public void setReceiveType(Integer receiveType){
		this.receiveType = receiveType;
	}

	public Integer getReceiveType(){
		return this.receiveType;
	}

	public void setReceiveUserId(String receiveUserId){
		this.receiveUserId = receiveUserId;
	}

	public String getReceiveUserId(){
		return this.receiveUserId;
	}

	public void setFileSize(Long fileSize){
		this.fileSize = fileSize;
	}

	public Long getFileSize(){
		return this.fileSize;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public String getFileName(){
		return this.fileName;
	}

	public void setFileType(Integer fileType){
		this.fileType = fileType;
	}

	public Integer getFileType(){
		return this.fileType;
	}

	public void setFileSuffix(String fileSuffix){
		this.fileSuffix = fileSuffix;
	}

	public String getFileSuffix(){
		return this.fileSuffix;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	@Override
	public String toString (){
		return "消息ID:"+(messageId == null ? "空" : messageId)+"，会议ID:"+(meetingId == null ? "空" : meetingId)+"，消息类型:"+(messageType == null ? "空" : messageType)+"，消息内容:"+(messageContent == null ? "空" : messageContent)+"，发送用户ID:"+(sendUserId == null ? "空" : sendUserId)+"，发送用户昵称:"+(sendUserNickName == null ? "空" : sendUserNickName)+"，发送时间:"+(sendTime == null ? "空" : sendTime)+"，接收类型:"+(receiveType == null ? "空" : receiveType)+"，接收用户ID:"+(receiveUserId == null ? "空" : receiveUserId)+"，文件大小:"+(fileSize == null ? "空" : fileSize)+"，文件名:"+(fileName == null ? "空" : fileName)+"，文件类型:"+(fileType == null ? "空" : fileType)+"，文件后缀:"+(fileSuffix == null ? "空" : fileSuffix)+"，状态:"+(status == null ? "空" : status);
	}
}
