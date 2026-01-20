package com.easymeeting.entity.po;

import java.util.Date;
import com.easymeeting.entity.enums.DateTimePatternEnum;
import com.easymeeting.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 
 */
public class MeetingReserve implements Serializable {


	/**
	 * 会议ID（主键）
	 */
	private String meetingId;

	/**
	 * 会议号（预约时生成，用于通过 preJoinMeeting 加入会议）
	 */
	private String meetingNo;

	/**
	 * 会议名称
	 */
	private String meetingName;

	/**
	 * 参与类型
	 */
	private Integer joinType;

	/**
	 * 参与密码
	 */
	private String joinPassword;

	/**
	 * 会议时长
	 */
	private Integer duration;

	/**
	 * 开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startTime;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 创建用户ID
	 */
	private String createUserId;

	/**
	 * 状态
	 */
	private Integer status;

	private String nickName;

	private String inviteUserIds;

	public String getInviteUserIds() {
		return inviteUserIds;
	}

	public void setInviteUserIds(String inviteUserIds) {
		this.inviteUserIds = inviteUserIds;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setMeetingId(String meetingId){
		this.meetingId = meetingId;
	}

	public String getMeetingId(){
		return this.meetingId;
	}

	public void setMeetingNo(String meetingNo){
		this.meetingNo = meetingNo;
	}

	public String getMeetingNo(){
		return this.meetingNo;
	}

	public void setMeetingName(String meetingName){
		this.meetingName = meetingName;
	}

	public String getMeetingName(){
		return this.meetingName;
	}

	public void setJoinType(Integer joinType){
		this.joinType = joinType;
	}

	public Integer getJoinType(){
		return this.joinType;
	}

	public void setJoinPassword(String joinPassword){
		this.joinPassword = joinPassword;
	}

	public String getJoinPassword(){
		return this.joinPassword;
	}

	public void setDuration(Integer duration){
		this.duration = duration;
	}

	public Integer getDuration(){
		return this.duration;
	}

	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

	public Date getStartTime(){
		return this.startTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
	}

	public void setCreateUserId(String createUserId){
		this.createUserId = createUserId;
	}

	public String getCreateUserId(){
		return this.createUserId;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	@Override
	public String toString (){
		return "会议ID（主键）:"+(meetingId == null ? "空" : meetingId)+"，会议名称:"+(meetingName == null ? "空" : meetingName)+"，参与类型:"+(joinType == null ? "空" : joinType)+"，参与密码:"+(joinPassword == null ? "空" : joinPassword)+"，会议时长:"+(duration == null ? "空" : duration)+"，开始时间:"+(startTime == null ? "空" : DateUtil.format(startTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，创建时间:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，创建用户ID:"+(createUserId == null ? "空" : createUserId)+"，状态:"+(status == null ? "空" : status);
	}
}
