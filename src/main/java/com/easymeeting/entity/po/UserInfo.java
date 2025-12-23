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
public class UserInfo implements Serializable {


	/**
	 * 
	 */
	private String userId;

	/**
	 * 
	 */
	private String email;

	/**
	 * 
	 */
	private String nickName;

	/**
	 * 
	 */
	private Integer sex;

	/**
	 * 
	 */
	private String password;

	/**
	 * 
	 */
	private Integer status;

	/**
	 * 
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

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


	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setNickName(String nickName){
		this.nickName = nickName;
	}

	public String getNickName(){
		return this.nickName;
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

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public Date getCreateTime(){
		return this.createTime;
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

	public  String getMeetingNo(){
		return this.meetingNo;
	}

	@Override
	public String toString (){
		return "userId:"+(userId == null ? "空" : userId)+"，email:"+(email == null ? "空" : email)+"，nickName:"+(nickName == null ? "空" : nickName)+"，sex:"+(sex == null ? "空" : sex)+"，password:"+(password == null ? "空" : password)+"，status:"+(status == null ? "空" : status)+"，createTime:"+(createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，lastLoginTime:"+(lastLoginTime == null ? "空" : lastLoginTime)+"，lastOffTime:"+(lastOffTime == null ? "空" : lastOffTime)+"，meetingNo:"+(meetingNo == null ? "空" : meetingNo);
	}
}
