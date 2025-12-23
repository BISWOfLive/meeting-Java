package com.easymeeting.entity.enums;


public enum UserStatusEnum {
    DISABLE(0, "启用"),
    ENABLE(1, "禁用");

    private Integer status;

    private String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static  UserStatusEnum getByStatus(String status){
        for (UserStatusEnum item : UserStatusEnum.values()) {
            if (item.getStatus().equals(status)){
                return item;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
