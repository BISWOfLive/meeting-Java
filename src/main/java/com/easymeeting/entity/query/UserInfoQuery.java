package com.easymeeting.entity.query;

import java.util.Date;


/**
 * 参数
 */
public class UserInfoQuery extends BaseParam {


	/**
	 * 
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 
	 */
	private String email;

	private String emailFuzzy;

	/**
	 * 
	 */
	private String nickName;

	private String nickNameFuzzy;

	/**
	 * 
	 */
	private Integer sex;

	/**
	 * 
	 */
	private String password;

	private String passwordFuzzy;

	/**
	 * 
	 */
	private Integer status;

	/**
	 * 
	 */
	private String createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 
	 */
	private Long lastLoginTime;

	/**
	 * 
	 */
	private Long lastOffTime;

	/**
	 * 
	 */
	private String meetingNo;

	private String meetingNoFuzzy;


	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setEmailFuzzy(String emailFuzzy){
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy(){
		return this.emailFuzzy;
	}

	public void setNickName(String nickName){
		this.nickName = nickName;
	}

	public String getNickName(){
		return this.nickName;
	}

	public void setNickNameFuzzy(String nickNameFuzzy){
		this.nickNameFuzzy = nickNameFuzzy;
	}

	public String getNickNameFuzzy(){
		return this.nickNameFuzzy;
	}

	public void setSex(Integer sex){
		this.sex = sex;
	}

	public Integer getSex(){
		return this.sex;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}

	public void setPasswordFuzzy(String passwordFuzzy){
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy(){
		return this.passwordFuzzy;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}

	public String getCreateTime(){
		return this.createTime;
	}

	public void setCreateTimeStart(String createTimeStart){
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart(){
		return this.createTimeStart;
	}
	public void setCreateTimeEnd(String createTimeEnd){
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd(){
		return this.createTimeEnd;
	}

	public void setLastLoginTime(Long lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}

	public Long getLastLoginTime(){
		return this.lastLoginTime;
	}

	public void setLastOffTime(Long lastOffTime){
		this.lastOffTime = lastOffTime;
	}

	public Long getLastOffTime(){
		return this.lastOffTime;
	}

	public void setMeetingNo(String meetingNo){
		this.meetingNo = meetingNo;
	}

	public String getMeetingNo(){
		return this.meetingNo;
	}

	public void setMeetingNoFuzzy(String meetingNoFuzzy){
		this.meetingNoFuzzy = meetingNoFuzzy;
	}

	public String getMeetingNoFuzzy(){
		return this.meetingNoFuzzy;
	}

}
