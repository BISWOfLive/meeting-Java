package com.easymeeting.entity.enums;

public enum MeetingJoinTypeEnum {
    NO_PASSWORD(0, "无需密码"),
    PASSWORD(1, "需要密码");
    int type;
    String desc;

    MeetingJoinTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static MeetingJoinTypeEnum getBypype(Integer type) {
        for (MeetingJoinTypeEnum item : MeetingJoinTypeEnum.values()) {
            if (item.getType().equals(type)) {
                return item;
            }

        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return desc;
    }
}