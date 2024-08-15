package com.starcloud.ops.business.app.domain.entity.chat;

import cn.hutool.extra.spring.SpringUtil;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigRespVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.*;
import com.starcloud.ops.business.app.domain.entity.skill.*;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.config.AppTypeEnum;
import com.starcloud.ops.business.app.enums.config.ChatExpandConfigEnum;
import com.starcloud.ops.business.app.service.chat.ChatExpandConfigService;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    private PrePromptConfigEntity prePromptConfig;
    private VariableEntity variable;
    private ModelConfigEntity modelConfig;
    private WebSearchConfigEntity webSearchConfig = new WebSearchConfigEntity();
    private List<DatesetEntity> datesetEntities;
    private SuggestedQuestionEntity suggestedQuestion;
    private OpeningStatementEntity openingStatement;
    private AudioConfigEntity audioConfig;
    private DescriptionEnity description;
    /**
     * 挂载的 gpt插件技能列表
     */
    private transient List<HandlerSkill> handlerSkills;
    /**
     * 挂载的 gpt插件技能列表
     */
    private transient List<GptPluginSkill> gptPluginSkills;
    /**
     * 挂载的 API技能列表
     */
    private transient List<ApiSkill> apiSkills;
    /**
     * 挂载的 应用技能列表
     */
    private transient List<AppWorkflowSkill> appWorkflowSkills;
    /**
     * 技能配置
     */
    private String appConfigId;

    @Override
    public void init() {

        if (StringUtils.isBlank(appConfigId)) {
            return;
        }

        ChatExpandConfigService bean = SpringUtil.getBean(ChatExpandConfigService.class);
        Map<Integer, List<ChatExpandConfigRespVO>> config = bean.getConfig(appConfigId);

        List<ChatExpandConfigRespVO> handlerConfig = config.get(ChatExpandConfigEnum.SYSTEM_HANDLER.getCode());

        this.handlerSkills = Optional.ofNullable(handlerConfig).orElse(new ArrayList<>()).stream()
                .filter(chatExpandConfigRespVO -> !chatExpandConfigRespVO.getDisabled() && chatExpandConfigRespVO.getSystemHandlerSkillDTO() != null)
                .map(chatExpandConfigRespVO -> {
                    HandlerSkill handlerSkill = ChatConfigConvert.INSTANCE.convert(chatExpandConfigRespVO.getSystemHandlerSkillDTO());
                    BaseToolHandler toolHandler = BaseToolHandler.of(chatExpandConfigRespVO.getSystemHandlerSkillDTO().getCode());
                    if (toolHandler != null) {
                        handlerSkill.setHandler(toolHandler);
                        handlerSkill.setEnabled(true);
                        return handlerSkill;
                    }

                    return null;

                }).filter(Objects::nonNull).collect(Collectors.toList());


        List<ChatExpandConfigRespVO> appWorkflow = config.get(ChatExpandConfigEnum.APP_WORKFLOW.getCode());
        this.appWorkflowSkills = Optional.ofNullable(appWorkflow).orElse(new ArrayList<>()).stream()
                .filter(chatExpandConfigRespVO -> !chatExpandConfigRespVO.getDisabled() && chatExpandConfigRespVO.getAppWorkflowSkillDTO() != null)
                .map(chatExpandConfigRespVO -> {
                    AppWorkflowSkill appWorkflowSkill = ChatConfigConvert.INSTANCE.convert(chatExpandConfigRespVO.getAppWorkflowSkillDTO());
                    AppEntity appEntity;
                    if (AppTypeEnum.APP.getCode().equals(chatExpandConfigRespVO.getAppWorkflowSkillDTO().getAppType())) {
                        appEntity = AppFactory.factoryApp(appWorkflowSkill.getSkillAppUid());
                    } else {
                        appEntity = AppFactory.factoryMarket(appWorkflowSkill.getSkillAppUid());
                    }

                    appWorkflowSkill.setApp(appEntity);
                    appWorkflowSkill.setEnabled(appEntity != null);
                    return appWorkflowSkill;
                }).collect(Collectors.toList());

    }

    /**
     * 校验实体，对一写复杂逻辑的校验，可以在这里实现
     */
    @Override
    public void validate(ValidateTypeEnum validateType) {

    }
}
