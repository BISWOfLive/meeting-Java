package com.easymeeting.entity.query;



/**
 * 参数
 */
public class MeetingReserveMemberQuery extends BaseParam {


	/**
	 * 会议ID（联合主键1）
	 */
	private String meetingId;

	private String meetingIdFuzzy;

	/**
	 * 被邀请用户ID（联合主键2）
	 */
	private String inviteUserId;

	private String inviteUserIdFuzzy;


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

	public void setInviteUserId(String inviteUserId){
		this.inviteUserId = inviteUserId;
	}

	public String getInviteUserId(){
		return this.inviteUserId;
	}

	public void setInviteUserIdFuzzy(String inviteUserIdFuzzy){
		this.inviteUserIdFuzzy = inviteUserIdFuzzy;
	}

	public String getInviteUserIdFuzzy(){
		return this.inviteUserIdFuzzy;
	}

}
