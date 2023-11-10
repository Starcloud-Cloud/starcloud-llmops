package com.starcloud.ops.business.app.api.plan.vo.request;

import com.starcloud.ops.business.app.api.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.enums.plan.CreativeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanReqVO", description = "创作计划请求")
public class CreativePlanReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 6864609752505405116L;

    /**
     * 创作计划名称
     */
    @Schema(description = "创作计划名称")
    @NotBlank(message = "创作计划：名称参数是必填项！")
    private String name;

    /**
     * 创作计划类型
     */
    @Schema(description = "创作计划类型")
    @NotBlank(message = "创作计划：类型参数是必填项！")
    @InEnum(value = CreativeTypeEnum.class, field = InEnum.EnumField.NAME, message = "创作计划：类型参数不支持({value})！")
    private String type;

    /**
     * 创作计划详细配置信息
     */
    @Schema(description = "创作计划详细配置信息")
    @Valid
    @NotNull(message = "创作计划：配置参数是必填项！")
    private CreativePlanConfigDTO config;

    /**
     * 创作计划描述
     */
    @Schema(description = "创作计划描述")
    private String description;


}
