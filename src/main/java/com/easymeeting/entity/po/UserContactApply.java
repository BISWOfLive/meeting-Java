package com.easymeeting.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;


/**
 * 用户联系人申请表
 */
public class UserContactApply implements Serializable {


    /**
     * 申请ID
     */
    private Integer applyId;

    /**
     * 申请人ID
     */
    private String applyUserId;

    /**
     * 接收人ID
     */
    private String receiveUserId;

    /**
     * 最后申请时间
     */
    private Long lastApplyTime;

    /**
     * 状态
     */
    private Integer status;

    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public Integer getApplyId() {
        return this.applyId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserId() {
        return this.applyUserId;
    }

    public void setReceiveUserId(String receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getReceiveUserId() {
        return this.receiveUserId;
    }

    public void setLastApplyTime(Long lastApplyTime) {
        this.lastApplyTime = lastApplyTime;
    }

    public Long getLastApplyTime() {
        return this.lastApplyTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "申请ID:" + (applyId == null ? "空" : applyId) + "，申请人ID:" + (applyUserId == null ? "空" : applyUserId) + "，接收人ID:" + (receiveUserId == null ? "空" : receiveUserId) + "，最后申请时间:" + (lastApplyTime == null ? "空" : lastApplyTime) + "，状态:" + (status == null ? "空" : status);
    }
}
