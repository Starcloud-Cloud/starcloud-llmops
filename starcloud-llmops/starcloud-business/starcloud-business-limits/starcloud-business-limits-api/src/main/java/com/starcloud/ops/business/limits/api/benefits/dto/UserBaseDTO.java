package com.starcloud.ops.business.limits.api.benefits.dto;

import lombok.Data;

/**
 *
 * 用户信息 DTO - 防止无法获取用户态信息
 *
 */
@Data
public class UserBaseDTO {


    /**
     * 创建人
     */
    private Long userId;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
