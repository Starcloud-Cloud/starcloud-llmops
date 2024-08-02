package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容执行请求")
public class CreativeContentExecuteReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 5788439976387194614L;

    /**
     * 创作内容UID集合
     */
    @Schema(description = "创作内容UID")
    private String uid;

    /**
     * 生成计划ID
     */
    @Schema(description = "生成计划ID")
    private String planUid;

    /**
     * 批次UID
     */
    @Schema(description = "批次UID")
    private String batchUid;

    /**
     * 创作内容类型
     */
    @Schema(description = "最大重试次数")
    private Integer maxRetry;

    /**
     * 是否强制执行
     */
    @Schema(description = "是否强制执行")
    private Boolean force;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private Long tenantId;

}
