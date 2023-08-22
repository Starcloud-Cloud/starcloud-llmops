package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.business.app.api.app.vo.request.config.skill.HandlerSkillVO;
import com.starcloud.ops.business.app.domain.entity.config.*;
import com.starcloud.ops.business.app.domain.entity.skill.*;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    @Override
    public void init() {

        Optional.ofNullable(this.getHandlerSkills()).orElse(new ArrayList<>()).stream().forEach(handlerSkill -> {
            if (handlerSkill.getHandler() == null) {
                handlerSkill.setHandler(BaseHandler.of(handlerSkill.getSkillName()));
            }
        });
    }


    private String code;

    private String prePrompt;

    private PrePromptConfigEntity prePromptConfig;

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
    private List<HandlerSkill> handlerSkills;

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
