package com.starcloud.ops.business.app.api.app.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-26
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用基础请求实体")
public class AppRequest implements Serializable {

    private static final long serialVersionUID = 1578944445567574534L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    @NotBlank(message = "应用名称不能为空")
    private String name;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    @NotBlank(message = "应用不能为空")
    private String model;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    @NotBlank(message = "应用类型不能为空")
    private String type;

    /**
     * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "应用来源类型")
    @NotBlank(message = "应用来源不能为空")
    private String source;

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
     * 应用图片
     */
    @Schema(description = "应用图片")
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息")
    private AppConfigDTO config;

    /**
     * 应用聊天配置信息
     */
    @Schema(description = "应用聊天配置信息")
    private AppChatConfigDTO chatConfig;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

}
