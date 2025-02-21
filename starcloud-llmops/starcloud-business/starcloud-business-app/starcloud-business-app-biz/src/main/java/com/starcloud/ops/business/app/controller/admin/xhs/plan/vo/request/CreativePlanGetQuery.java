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
@Schema(description = "获取创作计划请求")
public class CreativePlanGetQuery implements java.io.Serializable {

    private static final long serialVersionUID = 7087527124175833592L;

    /**
     * 创作计划uid
     */
    @Schema(description = "创作计划uid")
    private String uid;

    /**
     * 应用uid
     */
    @Schema(description = "应用uid")
    @NotBlank(message = "应用UID为必填项！")
    private String appUid;

    /**
     * 创作计划来源
     */
    @Schema(description = "创作计划来源")
    @NotBlank(message = "创作计划来源为必填项！")
    private String source;

    private String styleUid;
}
