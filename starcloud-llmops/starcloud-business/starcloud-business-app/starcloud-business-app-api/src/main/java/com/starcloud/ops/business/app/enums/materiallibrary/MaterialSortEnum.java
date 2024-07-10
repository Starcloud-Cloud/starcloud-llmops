package com.starcloud.ops.business.app.enums.materiallibrary;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 素材类型枚举
 */
@Getter
@AllArgsConstructor
public enum MaterialSortEnum {

    USER_COUNT("use_count", "使用次数"),

    CREATE_TIME("create_time", "创建时间"),

    UPDATE_TIME("update_time", "更新时间"),
    ;


    private String column;

    private String desc;

    public static String getColumn(String filed) {
        for (MaterialSortEnum value : MaterialSortEnum.values()) {
            if (value.name().equals(filed)) {
                return value.getColumn();
            }
        }
        return CREATE_TIME.getColumn();
    }


}
