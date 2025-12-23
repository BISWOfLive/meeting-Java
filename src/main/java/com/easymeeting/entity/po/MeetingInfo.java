package com.easymeeting.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.easymeeting.entity.enums.DateTimePatternEnum;
import com.easymeeting.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 
 */
public class MeetingInfo implements Serializable {


	/**
	 * 
	 */
	private String meetingId;

	/**
	 * 
	 */
	private String meetingNo;

	/**
	 * 
	 */
	private String meetingName;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 
	 */
	private String createUserId;

	/**
	 * 
	 */
	private Integer joinType;

	/**
	 * 
	 */
	private String joinPassword;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

	/**
	 * 
	 */
	private Integer status;

	private Integer memberCount;

	public Integer getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(Integer memberCount) {
		this.memberCount = memberCount;
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

	public void setJoinType(Integer joinType){
		this.joinType = joinType;
	}

	public Integer getJoinType(){
		return this.joinType;
	}

	public void setJoinPassword(String joinPassword){
		this.joinPassword = joinPassword;
	}

	public String getJoinPassword(){return this.joinPassword;}

	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

	public Date getStartTime(){
		return this.startTime;
	}

	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

	public Date getEndTime(){
		return this.endTime;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	@Override
	public String toString (){
		return "meetingId:"+(meetingId == null ? "空" : meetingId)+"，meetingNo:"+(meetingNo == null ? "空" : meetingNo)+"，meetingName:"+(meetingName == null ? "空" : meetingName)+"，createTime:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，createUserId:"+(createUserId == null ? "空" : createUserId)+"，joinType:"+(joinType == null ? "空" : joinType)+"，joinPassword:"+(joinPassword == null ? "空" : joinPassword)+"，startTime:"+(startTime == null ? "空" : DateUtil.format(startTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，endTime:"+(endTime == null ? "空" : DateUtil.format(endTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，status:"+(status == null ? "空" : status);
	}
}
