package com.easymeeting.entity.enums;

public enum MeetingStatusEnum {
    RUNING(0,"会议进行中"),
    FINISHEN(1,"会议已结束");

    private Integer status;
    private String desc;

    MeetingStatusEnum(Integer status,String desc){
        this.status = status;
        this.desc = desc;
    }

    public static MeetingStatusEnum getInstance(Integer status){
        for (MeetingStatusEnum value : MeetingStatusEnum.values()) {
            if (value.getStatus().equals(status)){
                return value;
            }
        }
        return  null;
    }


    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
