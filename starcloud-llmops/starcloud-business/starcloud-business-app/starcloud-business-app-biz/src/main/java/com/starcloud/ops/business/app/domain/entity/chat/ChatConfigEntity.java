package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.business.app.domain.entity.config.*;
import com.starcloud.ops.business.app.domain.entity.skill.ApiSkill;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkill;
import com.starcloud.ops.business.app.domain.entity.skill.BaseSkillEntity;
import com.starcloud.ops.business.app.domain.entity.skill.GptPluginSkill;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@EqualsAndHashCode
public class ChatConfigEntity extends BaseConfigEntity {

    private String code;

    private String prePrompt;

    private VariableEntity variable;

    private ModelConfigEntity modelConfig;

    private WebSearchConfigEntity webSearchConfig;

    private List<DatesetEntity> datesetEntities;

    private SuggestedQuestionEntity suggestedQuestion;

    private OpeningStatementEntity openingStatement;

    private AudioConfigEntity audioConfig;

    private DescriptionEnity description;

    /**
     * 挂载的 gpt插件技能列表
     */
    private List<GptPluginSkill> gptPluginSkills;

    /**
     * 挂载的 API技能列表
     */
    private List<ApiSkill> apiSkills;

    /**
     * 挂载的 应用技能列表
     */
    private List<AppWorkflowSkill> appWorkflowSkills;


    /**
     * 校验实体，对一写复杂逻辑的校验，可以在这里实现
     */
    @Override
    public void validate() {

    }
}
