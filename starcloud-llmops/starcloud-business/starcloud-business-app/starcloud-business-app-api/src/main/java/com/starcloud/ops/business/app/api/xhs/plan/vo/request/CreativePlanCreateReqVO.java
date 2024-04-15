package com.starcloud.ops.business.app.api.xhs.plan.vo.request;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
public class CreativePlanCreateReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 6864609752505405116L;

    /**
     * 创作计划详细配置信息
     */
    @Schema(description = "创作计划应用配置信息")
    @Valid
    @NotNull(message = "创作计划应用配置信息不能为空！")
    private CreativePlanConfigurationDTO configuration;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    @NotNull(message = "生成数量不能为空！")
    @Min(value = 1, message = "生成数量最小值为 1")
    @Max(value = 100, message = "生成数量最大值为 100")
    private Integer totalCount;

    /**
     * 检验
     */
    public void validate() {
        AppValidate.notNull(totalCount, "生成数量不能为空！");
        configuration.validate();
    }
}
