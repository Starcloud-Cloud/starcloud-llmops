package com.starcloud.ops.business.app.api.app.vo.request.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.ApiSkillVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.AppWorkflowSkillVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.GptPluginSkillVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.skill.HandlerSkillVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 函数请求对象")
public class ChatConfigReqVO extends BaseConfigReqVO {

    private static final long serialVersionUID = -1232191599886098743L;

    /**
     * code
     */
    @Schema(description = "code")
    private String code;

    @Schema(description = "对话提示词")
    private String prePrompt;

    @Schema(description = "对话提示词配置")
    private PrePromptConfigVO prePromptConfig;

    @Schema(description = "模版变量")
    private VariableReqVO variable;

    @Schema(description = "聊天模型参数配置")
    private ModelConfigReqVO modelConfig;

    @Schema(description = "绑定数据集")
    private List<DatesetReqVO> datesetEntities;

    @Schema(description = "聊天建议")
    private SuggestedQuestionReqVO suggestedQuestion;

    @Schema(description = "聊天欢迎语")
    private OpeningStatementReqVO openingStatement;

    @Schema(description = "常用问题")
    private List<CommonQuestionReqVO> commonQuestion;

    @Schema(description = "语音配置")
    private AudioConfigReqVO audioConfig;

    @Schema(description = "描述配置")
    private DescriptionReqVo description;

    @Schema(description = "联网")
    private WebSearchConfigReqVO webSearchConfig;


    @Schema(description = "系统技能")
    private List<HandlerSkillVO> handlerSkills;

    /**
     * 挂载的 gpt插件技能列表
     */
    @Schema(description = "GPT插件技能")
    private List<GptPluginSkillVO> gptPluginSkills;

    /**
     * 挂载的 API技能列表
     */
    @Schema(description = "API技能")
    private List<ApiSkillVO> apiSkills;

    /**
     * 挂载的 应用技能列表
     */
    @Schema(description = "AI应用技能")
    private List<AppWorkflowSkillVO> appWorkflowSkills;

    /**
     * 技能配置
     */
    private String appConfigId;

}
