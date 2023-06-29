package com.starcloud.ops.business.app.api.app.vo.request.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用变量请求对象")
public class VariableItemReqVO implements Serializable {

    private static final long serialVersionUID = 6502979238694503699L;

    /**
     * 变量 label
     */
    @Schema(description = "变量 label", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变量 label 不能为空")
    private String label;

    /**
     * 变量 field
     */
    @Schema(description = "变量 field", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变量 field 不能为空")
    private String field;

    /**
     * 变量类型
     */
    @Schema(description = "变量类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变量类型不能为空")
    @InEnum(value = AppVariableTypeEnum.class, message = "变量类型[{value}]必须属于: {values}")
    private String type;

    /**
     * 变量样式
     */
    @Schema(description = "变量样式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变量样式不能为空")
    @InEnum(value = AppVariableStyleEnum.class, message = "变量样式[{value}]必须属于: {values}")
    private String style;

    /**
     * 变量分组
     */
    @Schema(description = "变量分组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变量分组不能为空")
    @InEnum(value = AppVariableGroupEnum.class, message = "变量分组[{value}]必须属于: {values}")
    private String group;

    /**
     * 变量排序
     */
    @Schema(description = "变量排序")
    private Integer order;

    /**
     * 变量默认值
     */
    @Schema(description = "变量默认值")
    private Object defaultValue;

    /**
     * 变量值
     */
    @Schema(description = "变量值")
    private Object value;

    /**
     * 变量是否显示
     */
    @Schema(description = "变量是否显示", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "变量是否显示不能为空")
    private Boolean isShow;

    /**
     * 变量是否为点位
     */
    @Schema(description = "变量是否为点位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "变量是否为点位不能为空")
    private Boolean isPoint;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 变量选项, 变量类型为 SELECT 时使用
     */
    @Schema(description = "变量选项")
    private List<Option> options;
}
