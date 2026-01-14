package com.easymeeting.entity.enums;

public enum ReceiveTypeEnum {
    ALL(0,"全员"),
    USER(1,"个人");

    private Integer type;
    private String msg;

    ReceiveTypeEnum(Integer type,String msg){
        this.msg = msg;
        this.type = type;
    }

    public static ReceiveTypeEnum getByType(Integer type){
        for (ReceiveTypeEnum item : ReceiveTypeEnum.values()) {
            if (item.getType().equals(type)){
                return item;
            }
        }
        return null;
    }


    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
