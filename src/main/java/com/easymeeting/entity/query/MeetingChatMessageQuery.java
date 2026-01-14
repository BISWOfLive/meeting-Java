package com.easymeeting.entity.query;



/**
 * 会议聊天消息表参数
 */
public class MeetingChatMessageQuery extends BaseParam {


	/**
	 * 消息ID
	 */
	private Long messageId;

	/**
	 * 会议ID
	 */
	private String meetingId;

	private String meetingIdFuzzy;

	/**
	 * 消息类型
	 */
	private Integer messageType;

	/**
	 * 消息内容
	 */
	private String messageContent;

	private String messageContentFuzzy;

	/**
	 * 发送用户ID
	 */
	private String sendUserId;

	private String sendUserIdFuzzy;

	/**
	 * 发送用户昵称
	 */
	private String sendUserNickName;

	private String sendUserNickNameFuzzy;

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

	private String receiveUserIdFuzzy;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名
	 */
	private String fileName;

	private String fileNameFuzzy;

	/**
	 * 文件类型
	 */
	private Integer fileType;

	/**
	 * 文件后缀
	 */
	private String fileSuffix;

	private String fileSuffixFuzzy;

	/**
	 * 状态
	 */
	private Integer status;

	private String userId;

	private Long maxMessageId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getMaxMessageId() {
		return maxMessageId;
	}

	public void setMaxMessageId(Long maxMessageId) {
		this.maxMessageId = maxMessageId;
	}

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

	public void setMeetingIdFuzzy(String meetingIdFuzzy){
		this.meetingIdFuzzy = meetingIdFuzzy;
	}

	public String getMeetingIdFuzzy(){
		return this.meetingIdFuzzy;
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

	public void setMessageContentFuzzy(String messageContentFuzzy){
		this.messageContentFuzzy = messageContentFuzzy;
	}

	public String getMessageContentFuzzy(){
		return this.messageContentFuzzy;
	}

	public void setSendUserId(String sendUserId){
		this.sendUserId = sendUserId;
	}

	public String getSendUserId(){
		return this.sendUserId;
	}

	public void setSendUserIdFuzzy(String sendUserIdFuzzy){
		this.sendUserIdFuzzy = sendUserIdFuzzy;
	}

	public String getSendUserIdFuzzy(){
		return this.sendUserIdFuzzy;
	}

	public void setSendUserNickName(String sendUserNickName){
		this.sendUserNickName = sendUserNickName;
	}

	public String getSendUserNickName(){
		return this.sendUserNickName;
	}

	public void setSendUserNickNameFuzzy(String sendUserNickNameFuzzy){
		this.sendUserNickNameFuzzy = sendUserNickNameFuzzy;
	}

	public String getSendUserNickNameFuzzy(){
		return this.sendUserNickNameFuzzy;
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

	public void setReceiveUserIdFuzzy(String receiveUserIdFuzzy){
		this.receiveUserIdFuzzy = receiveUserIdFuzzy;
	}

	public String getReceiveUserIdFuzzy(){
		return this.receiveUserIdFuzzy;
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

	public void setFileNameFuzzy(String fileNameFuzzy){
		this.fileNameFuzzy = fileNameFuzzy;
	}

	public String getFileNameFuzzy(){
		return this.fileNameFuzzy;
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

	public void setFileSuffixFuzzy(String fileSuffixFuzzy){
		this.fileSuffixFuzzy = fileSuffixFuzzy;
	}

	public String getFileSuffixFuzzy(){
		return this.fileSuffixFuzzy;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

}
