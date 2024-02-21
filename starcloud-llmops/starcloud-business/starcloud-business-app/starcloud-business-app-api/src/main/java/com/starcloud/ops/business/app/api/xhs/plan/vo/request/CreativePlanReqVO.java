package com.starcloud.ops.business.app.api.xhs.plan.vo.request;

import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeRandomTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
     * 执行随机方式
     */
    @Schema(description = "执行随机方式")
    @NotBlank(message = "执行随机方式不能为空！")
    @InEnum(value = CreativeRandomTypeEnum.class, field = InEnum.EnumField.NAME, message = "执行随机方式不支持({value})！")
    private String randomType;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    @NotNull(message = "生成数量不能为空！")
    @Min(value = 1, message = "生成数量最小值为 1")
    @Max(value = 100, message = "生成数量最大值为 500")
    private Integer total;

    /**
     * 创作计划描述
     */
    @Schema(description = "创作计划描述")
    private String description;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private List<String> tags;

}
