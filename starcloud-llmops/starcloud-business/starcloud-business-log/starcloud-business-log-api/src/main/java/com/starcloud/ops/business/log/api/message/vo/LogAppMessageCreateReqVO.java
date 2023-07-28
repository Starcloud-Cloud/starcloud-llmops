package com.starcloud.ops.business.log.api.message.vo;

import lombok.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理后台 - 应用执行日志结果创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessageCreateReqVO extends LogAppMessageBaseVO {

    /**
     * 多租户编号
     */
    private Long tenantId;

    /**
     * 指定创建者
     * 1，游客执行的时候，创建者为 执行应用的 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;


}