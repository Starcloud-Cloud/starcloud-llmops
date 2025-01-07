package com.starcloud.ops.business.app.api.app.vo.request.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    @Schema(description = "变量类型")
    private String type;

    /**
     * 变量样式
     */
    @Schema(description = "变量样式")
    private String style;

    /**
     * 变量分组
     */
    @Schema(description = "变量分组")
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
    private Boolean isPoint;

    /**
     * 是否升级<br>
     * 如果该值为 true, 则保留用户配置的值，否则则使用系统默认值。
     */
    private Boolean isKeepUserValue;

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

    /**
     * 数量
     */
    @Schema(description = "数量")
    private Integer count;

    public void setType(String type) {
        if (StringUtil.isBlank(type)) {
            this.type = AppVariableTypeEnum.TEXT.name();
        } else {
            this.type = type;
        }
    }

    public void setGroup(String group) {
        if (StringUtil.isBlank(group)) {
            this.group = AppVariableGroupEnum.PARAMS.name();
        } else {
            this.group = group;
        }
    }

    public void setPoint(Boolean point) {
        if (Objects.isNull(point)) {
            this.isPoint = Boolean.FALSE;
        } else {
            isPoint = point;
        }
    }
}
