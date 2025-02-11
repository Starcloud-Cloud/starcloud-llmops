package com.starcloud.ops.business.app.controller.admin.plugins.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "插件配置")
public class PluginConfigVO {

    /**
     * 素材库uid
     */
    @Schema(description = "素材库uid")
    @NotBlank(message = "素材库uid 不能为空")
    private String libraryUid;

    @Schema(description = "插件uid")
    @NotBlank(message = "插件uid 不能为空")
    private String pluginUid;

    @Schema(description = "绑定名称")
    @NotBlank(message = "绑定名称 不能为空")
    @Length(max = 10,message = "绑定名称最长不可超过 10")
    private String bindName;

    /**
     * 字段映射
     */
    @Schema(description = "字段映射配置")
    private String fieldMap;

    /**
     * 执行参数
     */
    @Schema(description = "执行参数配置")
    private String executeParams;

    private Integer type;
}
