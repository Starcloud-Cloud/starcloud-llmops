package com.starcloud.ops.business.enums;

import lombok.Getter;

@Getter
public enum NotificationSortFieldEnum {

    endTime("n.end_time", "结束时间"),

    singleBudget("n.single_budget", "单个任务预算"),

    updateTime("n.update_time", "更新时间"),

    claimCount("claimCount", "领取量"),
            ;

    private String column;

    private String desc;

    NotificationSortFieldEnum(String column, String desc) {
        this.column = column;
        this.desc = desc;
    }


    public static String getColumn(String filed) {
        for (NotificationSortFieldEnum value : NotificationSortFieldEnum.values()) {
            if (value.name().equals(filed)) {
                return value.getColumn();
            }
        }
        return updateTime.getColumn();
    }

}
