package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.business.app.domain.entity.config.*;
import com.starcloud.ops.business.app.domain.entity.skill.BaseSkillEntity;
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

    private AudioTransciptEntity audioTransciptEntity;

    private DescriptionEnity description;

    /**
     * 挂载的 技能列表
     */
    private List<BaseSkillEntity> skills;

    /**
     * 校验实体，对一写复杂逻辑的校验，可以在这里实现
     */
    @Override
    public void validate() {

    }
}
