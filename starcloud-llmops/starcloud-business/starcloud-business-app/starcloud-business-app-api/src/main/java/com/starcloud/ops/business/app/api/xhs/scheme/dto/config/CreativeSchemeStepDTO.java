package com.starcloud.ops.business.app.api.xhs.scheme.dto.config;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "自定义创作步骤配置")
public class CreativeSchemeStepDTO implements java.io.Serializable {

    private static final long serialVersionUID = -1288796330380477644L;

    /**
     * 创作方案步骤ID
     */
    @Schema(description = "创作方案步骤ID")
    private String id;

    /**
     * 创作方案步骤名称
     */
    @Schema(description = "创作方案步骤名称")
    private String name;

    /**
     * 创作方案步骤生成模式
     */
    @Schema(description = "创作方案步骤生成模式")
    private String generateMode;

    /**
     * 创作方案步骤要求
     */
    @Schema(description = "创作方案步骤要求")
    private String requirement;

    /**
     * 创作方案步骤变量
     */
    @Schema(description = "创作方案步骤变量")
    private VariableItemDTO variable;

}
