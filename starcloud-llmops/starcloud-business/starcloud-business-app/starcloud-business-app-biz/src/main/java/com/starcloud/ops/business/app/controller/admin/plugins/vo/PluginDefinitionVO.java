package com.starcloud.ops.business.app.controller.admin.plugins.vo;

import com.starcloud.ops.business.app.enums.plugin.OutputTypeEnum;
import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.enums.plugin.PluginSceneEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "插件配置")
public class PluginDefinitionVO {

    @Schema(description = "插件名称")
    @NotBlank(message = "插件名称不能为空")
    @Size(min = 2, max = 20, message = "用户账号长度为 2-20 个字符")
    private String pluginName;

    @Schema(description = "图片")
    private String avatar;

    @Schema(description = "场景")
    @NotBlank(message = "场景不能为空")
    @InEnum(value = PluginSceneEnum.class, field = InEnum.EnumField.CODE, message = "场景[{value}]必须在: [{values}] 范围内！")
    private String scene;

    @Schema(description = "输入")
    @NotBlank(message = "输入不能为空")
    private String input;

    @Schema(description = "输入结构")
    private String inputFormart;

    @Schema(description = "输出")
    @NotBlank(message = "输出不能为空")
    private String output;

    @Schema(description = "输出结构")
    private String outputFormart;

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

    @Schema(description = "描述")
    private String description;

    @Schema(description = "验证成功")
    private Boolean verifyState;

    @Schema(description = "输出类型")
    @InEnum(value = OutputTypeEnum.class, field = InEnum.EnumField.CODE, message = "输出类型[{value}]必须在: [{values}] 范围内！")
    private String outputType;

    @Schema(description = "是否发布")
    private Boolean published;

    @Schema(description = "执行总时间")
    private Long totalTime;

    @Schema(description = "执行次数")
    private Integer count;

    @Schema(description = "平均执行时间 ms")
    private Long executeTimeAvg;

    @Schema(description = "开启ai识别")
    private Boolean enableAi;

    @Schema(description = "用户自定义提示词")
    private String userPrompt;

    @Schema(description = "用户输入")
    private String userInput;

    @Schema(description = "识别结果")
    private String aiResult;

}
