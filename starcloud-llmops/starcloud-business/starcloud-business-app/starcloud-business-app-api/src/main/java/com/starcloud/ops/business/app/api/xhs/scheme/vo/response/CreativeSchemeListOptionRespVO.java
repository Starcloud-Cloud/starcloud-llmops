package com.starcloud.ops.business.app.api.xhs.scheme.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创作方案DO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作方案详情")
public class CreativeSchemeListOptionRespVO implements java.io.Serializable {

    private static final long serialVersionUID = 347884633370912370L;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String uid;

    /**
     * 创作方案名称
     */
    @Schema(description = "创作方案名称")
    private String name;

    /**
     * 创作方案模式
     */
    @Schema(description = "创作方案模式")
    private String mode;

    /**
     * 创作方案描述
     */
    @Schema(description = "创作方案描述")
    private String description;

    /**
     * 创作方案配置信息
     */
    @Schema(description = "创作方案变量信息")
    private List<VariableItemRespVO> variables;

    /**
     * 创作方案步骤配置信息
     */
    @Schema(description = "方案步骤配置信息")
    private List<BaseSchemeStepDTO> steps;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
