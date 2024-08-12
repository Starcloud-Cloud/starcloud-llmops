package com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作计划列表请求")
public class CreativePlanListQuery implements java.io.Serializable {

    private static final long serialVersionUID = 8118507177135519788L;

    /**
     * 创作计划uid
     */
    @Schema(description = "创作计划uid")
    private String uid;

    @Schema(description = "来源  APP/MARKET")
    @NotBlank(message = "来源 不能为空")
    private String source;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "limit")
    private int limit = 100;

    /**
     * 创作计划状态
     */
    @Schema(description = "创作计划状态")
    private String status;

}
