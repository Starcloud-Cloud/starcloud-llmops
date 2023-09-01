package com.starcloud.ops.business.app.api.chat.config.vo;

import cn.hutool.json.JSONObject;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.app.api.chat.config.dto.*;
import com.starcloud.ops.business.app.enums.config.ChatExpandConfigEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "聊天配置")
public class ChatExpandConfigReqVO {

    @Schema(description = "配置uid")
    private String uid;

    @Schema(description = "appConfigId")
    private String appConfigId;

    @Schema(description = "配置类型")
    @InEnum(value = ChatExpandConfigEnum.class)
    @NotNull(message = "配置类型不能为空")
    private Integer type;

    @Schema(description = "开启/关闭")
    private Boolean disabled;

    @Schema(description = "菜单配置")
    private ChatMenuConfigDTO chatMenuConfigDTO;

    @Schema(description = "API技能配置")
    private ApiSkillDTO apiSkillDTO;

    @Schema(description = "应用技能配置")
    private AppWorkflowSkillDTO appWorkflowSkillDTO;

    @Schema(description = "gpt插件技能配置")
    private GptPluginSkillDTO gptPluginSkillDTO;

    @Schema(description = "系统技能配置")
    private SystemHandlerSkillDTO systemHandlerSkillDTO;


}
