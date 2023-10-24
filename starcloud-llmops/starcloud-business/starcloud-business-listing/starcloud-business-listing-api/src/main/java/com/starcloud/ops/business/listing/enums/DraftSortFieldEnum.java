package com.starcloud.ops.business.listing.enums;

import lombok.Getter;

@Getter
public enum DraftSortFieldEnum {

    score("score"),
    createTime("create_time"),
    updateTime("update_time"),
    ;

    private String column;


    DraftSortFieldEnum(String column) {
        this.column = column;
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
