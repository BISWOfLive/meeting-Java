package com.easymeeting.entity.enums;

public enum MemberTypeEnum {
    COMPERE(1, "主持人"),
    NORMAL(2, "普通成员");
    private Integer type;
    private String name;
    MemberTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
    public Integer getType() {
        return type;
    }
    public String getName() {
        return name;
    }
}
