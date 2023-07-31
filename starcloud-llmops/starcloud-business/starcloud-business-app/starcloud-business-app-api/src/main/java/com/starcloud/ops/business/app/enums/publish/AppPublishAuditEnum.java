package com.starcloud.ops.business.app.enums.publish;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 应用市场审核步骤类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@SuppressWarnings("unused")
public enum AppPublishAuditEnum implements IEnumable<Integer> {

    /**
     * 未发布
     */
    UN_PUBLISH(0, "未发布"),

    /**
     * 未审核
     */
    PENDING(1, "待审核"),

    /**
     * 审核通过
     */
    APPROVED(2, "审核通过"),

    /**
     * 审核不通过
     */
    REJECTED(3, "审核不通过"),

    /**
     * 用户已取消
     */
    CANCELED(4, "用户已取消"),

    /**
     * 已失效
     */
    INVALID(5, "已失效");

    /**
     * 步骤类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 步骤类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  步骤类型 Code
     * @param label 步骤类型说明
     */
    AppPublishAuditEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
