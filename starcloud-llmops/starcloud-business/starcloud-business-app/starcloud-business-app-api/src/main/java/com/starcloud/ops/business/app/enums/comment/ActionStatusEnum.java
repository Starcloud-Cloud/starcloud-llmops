package com.starcloud.ops.business.app.enums.comment;

import cn.hutool.core.util.ArrayUtil;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户操作类型枚举
 */
@Getter
@AllArgsConstructor
public enum ActionStatusEnum implements IEnumable<Integer> {

    NO_EXECUTION(0, "未执行", 1),

    MISS(9, "未命中", 5),

    MANUAL(9, "手动执行", 9),
    SUCCESS_MATCH(10, "命中成功", 10),

    WAIT_SEND(15, "待发送", 15),

    SUCCESS_SEND(20, " 发送成功", 20),
    ;

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 标签
     */
    private final String label;

    /**
     * 顺序
     */
    private final Integer order;


    public static ActionStatusEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
