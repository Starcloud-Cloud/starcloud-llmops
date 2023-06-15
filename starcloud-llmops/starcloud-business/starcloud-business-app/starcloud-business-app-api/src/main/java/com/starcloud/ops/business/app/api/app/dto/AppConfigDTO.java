package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode
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
     * 应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用
     */
    @Schema(description = "应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用")
    private String type;

    /**
     * 应用来源类型：表示应用的是从那个平台创建，或者下载的。
     */
    @Schema(description = "应用来源类型：表示应用的是从那个平台创建，或者下载的。")
    private String source;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签，多个以逗号分割")
    private List<String> tags;

    /**
     * 应用类别，多个以逗号分割
     */
    @Schema(description = "应用类别，多个以逗号分割")
    private List<String> categories;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用类别，多个以逗号分割")
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
