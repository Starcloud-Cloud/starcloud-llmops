package com.starcloud.ops.business.listing.enums;

import lombok.Getter;

@Getter
public enum DraftSortFieldEnum {

    score("score", "分数"),

    createTime("create_time", "创建时间"),

    updateTime("update_time", "更新时间"),
    ;

    private String column;

    private String desc;


    DraftSortFieldEnum(String column, String desc) {
        this.column = column;
        this.desc = desc;
    }

    public static String getColumn(String filed) {
        for (DraftSortFieldEnum value : DraftSortFieldEnum.values()) {
            if (value.name().equals(filed)) {
                return value.getColumn();
            }
        }
        return createTime.getColumn();
    }
}
