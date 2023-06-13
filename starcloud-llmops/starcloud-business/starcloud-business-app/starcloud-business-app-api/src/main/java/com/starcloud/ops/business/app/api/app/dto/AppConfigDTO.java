package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIGC 应用配置DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用配置DTO")
public class AppConfigDTO implements Serializable {

    private static final long serialVersionUID = 1575558145567574534L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用类型, 0：系统推荐应用，1：我的应用，2：下载应用
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 应用标识, 区分自定义应用和每一种具体的系统应用，所有的应用的具体类型都基于此标识，不同的标识，应用的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "应用标识")
    private String logotype;

    /**
     * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "应用来源类型")
    private String sourceType;

    /**
     * 应用版本，默认版本 1.0.0
     */
    @Schema(description = "应用版本")
    private String version;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别")
    private List<String> categories;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private List<String> scenes;

    /**
     * 应用步骤
     */
    @Schema(description = "应用步骤")
    private List<StepWrapperDTO> steps;

    /**
     * 应用变量
     */
    @Schema(description = "应用变量")
    private List<VariableDTO> variables;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

}
