package com.easymeeting.entity.vo;

import java.io.Serializable;

public class UserInfoVo implements Serializable {

    private String userId;
    private String nickName;
    private Integer sex;
    private String token;
    private String meetingNo;
    private Boolean admin;

    public UserInfoVo() {
    }

    public UserInfoVo(String userId, String nickName, Integer sex, String token, String meetingNo, Boolean admin) {
        this.userId = userId;
        this.nickName = nickName;
        this.sex = sex;
        this.token = token;
        this.meetingNo = meetingNo;
        this.admin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMeetingNo() {
        return meetingNo;
    }

    public void setMeetingNo(String meetingNo) {
        this.meetingNo = meetingNo;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
