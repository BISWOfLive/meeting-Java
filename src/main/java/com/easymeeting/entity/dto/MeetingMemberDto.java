package com.easymeeting.entity.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingMemberDto implements Serializable {
    private String userid;
    private String nickName;
    private String avatar;
    private Long joinTime;
    private Integer memberType;
    private Integer status;
    private Boolean openVideo;
    private Integer sex;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public Integer getMemberType() {
        return memberType;
    }

    public void setMemberType(Integer memberType) {
        this.memberType = memberType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getOpenVideo() {
        return openVideo;
    }

    public void setOpenVideo(Boolean openVideo) {
        this.openVideo = openVideo;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
}