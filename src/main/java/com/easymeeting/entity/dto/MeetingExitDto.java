package com.easymeeting.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MeetingExitDto implements Serializable {
    private String exitUserId;
    private List<MeetingMemberDto> meetingMemberList;
    private Integer exitStatus;

    public Integer getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(Integer exitStatus) {
        this.exitStatus = exitStatus;
    }

    public String getExitUserId() {
        return exitUserId;
    }

    public void setExitUserId(String exitUserId) {
        this.exitUserId = exitUserId;
    }

    public List<MeetingMemberDto> getMeetingMemberList() {
        return meetingMemberList;
    }

    public void setMeetingMemberList(List<MeetingMemberDto> meetingMemberList) {
        this.meetingMemberList = meetingMemberList;
    }
}