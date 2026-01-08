package com.easymeeting.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;


/**
 * 
 */
public class MeetingReserveMember implements Serializable {


	/**
	 * 会议ID（联合主键1）
	 */
	private String meetingId;

	/**
	 * 被邀请用户ID（联合主键2）
	 */
	private String inviteUserId;


	public void setMeetingId(String meetingId){
		this.meetingId = meetingId;
	}

	public String getMeetingId(){
		return this.meetingId;
	}

	public void setInviteUserId(String inviteUserId){
		this.inviteUserId = inviteUserId;
	}

	public String getInviteUserId(){
		return this.inviteUserId;
	}

	@Override
	public String toString (){
		return "会议ID（联合主键1）:"+(meetingId == null ? "空" : meetingId)+"，被邀请用户ID（联合主键2）:"+(inviteUserId == null ? "空" : inviteUserId);
	}
}
