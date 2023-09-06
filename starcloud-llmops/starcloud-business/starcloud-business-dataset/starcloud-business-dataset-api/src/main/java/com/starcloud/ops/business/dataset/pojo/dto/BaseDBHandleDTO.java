package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 数据库基础字段 DTO 用作手动设置数据库中的创建人、更新人、租户信息
 */
@Data
@ToString
public class BaseDBHandleDTO {

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 租户 ID
     */
    private Long tenantId;

    /**
     * 游客
     */
    private Long endUser;

}
