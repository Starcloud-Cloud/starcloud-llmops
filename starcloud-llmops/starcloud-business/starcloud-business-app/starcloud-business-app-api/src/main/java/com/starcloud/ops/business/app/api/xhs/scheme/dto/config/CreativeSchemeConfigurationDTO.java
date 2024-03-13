package com.starcloud.ops.business.app.api.xhs.scheme.dto.config;

import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "自定义创作方案配置")
public class CreativeSchemeConfigurationDTO implements java.io.Serializable {

    private static final long serialVersionUID = -1278431625060498370L;

    /**
     * 创作应用UID
     */
    @Schema(description = "创作应用UID")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "创作应用名称")
    private String appName;

    /**
     * 应用描述
     */
    @Schema(description = "创作应用描述")
    private String description;

    /**
     * 应用标签
     */
    @Schema(description = "创作应用标签")
    private List<String> tags;

    /**
     * 步骤数量
     */
    @Schema(description = "创作应用步骤数量")
    private Integer stepCount;

    /**
     * 应用版本号
     */
    @Schema(description = "创作应用版本号")
    private Integer version;

    /**
     * 应用示例
     */
    @Schema(description = "示例")
    private String example;

    /**
     * 抽象的 创作方案 流程节点配置
     */
    private List<BaseSchemeStepDTO> steps;

    /**
     * 验证
     */
    public void validate() {
        AppValidate.notBlank(appUid, "缺少必填项：创作模板！");
        AppValidate.notEmpty(steps, "创作模板配置异常，请联系管理员！");
        steps.forEach(BaseSchemeStepDTO::validate);
    }
}
