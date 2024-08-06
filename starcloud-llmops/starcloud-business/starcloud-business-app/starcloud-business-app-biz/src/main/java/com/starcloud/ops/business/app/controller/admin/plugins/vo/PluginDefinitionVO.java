package com.starcloud.ops.business.app.controller.admin.plugins.vo;

import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginSceneEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Schema(description = "插件配置")
public class PluginDefinitionVO {

    @Schema(description = "插件名称")
    @NotBlank(message = "用户账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "用户账号由 数字、字母 组成")
    @Size(min = 4, max = 20, message = "用户账号长度为 4-20 个字符")
    private String pluginName;

    @Schema(description = "场景")
    @NotBlank(message = "场景不能为空")
    @InEnum(value = PluginSceneEnum.class, field = InEnum.EnumField.CODE, message = "场景[{value}]必须在: [{values}] 范围内！")
    private String scene;

    @Schema(description = "输入")
    @NotBlank(message = "输入不能为空")
    private String input;

    @Schema(description = "输出")
    @NotBlank(message = "输出不能为空")
    private String output;

    @Schema(description = "实现类型")
    @NotBlank(message = "实现类型不能为空")
    @InEnum(value = PlatformEnum.class, field = InEnum.EnumField.CODE, message = "实现类型[{value}]必须在: [{values}] 范围内！")
    private String type;

    @Schema(description = "coze 访问令牌Id")
    private String cozeTokenId;

    @Schema(description = "空间Id")
    private String spaceId;

    @Schema(description = "机器人id/应用市场uid")
    @NotBlank(message = "机器人id/应用市场uid不能为空")
    private String entityUid;

    @Schema(description = "机器人名称 或 应用名称")
    private String entityName;

}
