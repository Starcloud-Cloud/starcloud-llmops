package com.starcloud.ops.business.app.api.xhs.plan.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CreativePlanModifyReqVO", description = "创作计划修改请求")
public class CreativePlanModifyReqVO extends CreativePlanReqVO {

    private static final long serialVersionUID = 7513433575049699291L;

    /**
     * 创作计划UID
     */
    @NotBlank(message = "创作计划UID不能为空！")
    @Schema(description = "创作计划UID")
    private String uid;
}