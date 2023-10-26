package com.starcloud.ops.business.listing.enums;

import lombok.Getter;

@Getter
public enum DictSortFieldEnum {
    count("count", "关键词个数"),

    createTime("create_time", "创建时间"),

    updateTime("update_time", "更新时间"),
    ;

    private final String column;

    private final String desc;

    DictSortFieldEnum(String column, String desc) {
        this.column = column;
        this.desc = desc;
    }

    public static String getColumn(String filed) {
        for (DictSortFieldEnum value : DictSortFieldEnum.values()) {
            if (value.name().equals(filed)) {
                return value.getColumn();
            }
        }
        return createTime.getColumn();
    }
}
