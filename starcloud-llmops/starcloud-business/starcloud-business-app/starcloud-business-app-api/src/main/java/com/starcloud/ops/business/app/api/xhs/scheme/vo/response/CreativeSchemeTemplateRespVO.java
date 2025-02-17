package com.starcloud.ops.business.app.api.xhs.scheme.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作方案详情")
public class CreativeSchemeTemplateRespVO implements java.io.Serializable {

    private static final long serialVersionUID = -2476793341336174186L;

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
     * 素材类型，素材库字段设置步骤中的类型，放到外层，方便处理。没有素材库字段设置，此值为空
     */
    @Schema(description = "素材类型")
    private String materialType;

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

}
