package com.starcloud.ops.business.app.enums.opus;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

@Getter
public enum OpusTypeEnum implements IEnumable<String> {
    CLICK_READ("CLICK_READ","点读卡片"),
    VIDEO_CARD("VIDEO_CARD","视频卡片"),
    CHILDREN_BOOK("CHILDREN_BOOK","儿童读物")

    ;


    private final String code;

    private final String desc;

    OpusTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return desc;
    }

}
