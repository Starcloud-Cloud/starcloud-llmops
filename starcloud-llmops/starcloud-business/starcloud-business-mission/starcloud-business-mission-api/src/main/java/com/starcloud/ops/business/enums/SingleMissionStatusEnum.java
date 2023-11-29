package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

@Getter
public enum SingleMissionStatusEnum implements IEnumable<String> {
    init("init","待发布"),
    stay_claim("stay_claim", "待认领"),
    claimed("claimed", "已认领"),
    published("published", "用户已发布"),
    pre_settlement("pre_settlement", "预结算"),
    settlement("settlement", "结算"),
    close("close", "关闭"),
    settlement_error("settlement_error","结算异常")
    ;

    private final String code;

    private final String desc;

    SingleMissionStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return code;
    }
}
