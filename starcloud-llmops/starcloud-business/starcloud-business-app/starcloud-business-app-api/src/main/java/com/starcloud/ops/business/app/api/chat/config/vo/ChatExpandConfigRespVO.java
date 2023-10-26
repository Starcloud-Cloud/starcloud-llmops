package com.starcloud.ops.business.app.api.chat.config.vo;


import com.starcloud.ops.business.app.api.chat.config.dto.ApiSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.AppWorkflowSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.ChatMenuConfigDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.GptPluginSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.SystemHandlerSkillDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "聊天配置")
public class ChatExpandConfigRespVO {

    @Schema(description = "配置uid")
    private String uid;

    @Schema(description = "appConfigId")
    private String appConfigId;

    @Schema(description = "配置类型")
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
