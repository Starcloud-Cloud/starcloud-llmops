package com.starcloud.ops.business.app.api.xhs.bath.vo.request;

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
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanBatchReqVO", description = "创作计划批次请求")
public class CreativePlanBatchReqVO implements java.io.Serializable {

    private static final long serialVersionUID = -5086971129458146294L;


    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String planUid;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    private String appUid;

    /**
     * 应用版本号
     */
    @Schema(description = "应用版本号")
    private Integer version;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private List<String> tags;

    /**
     * 创作计划详细配置信息
     */
    @Schema(description = "创作计划配置信息")
    @Valid
    @NotNull(message = "创作计划配置信息不能为空！")
    private CreativePlanConfigurationDTO configuration;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    @NotNull(message = "生成数量不能为空！")
    @Min(value = 1, message = "生成数量最小值为 1")
    @Max(value = 100, message = "生成数量最大值为 100")
    private Integer totalCount;

}
