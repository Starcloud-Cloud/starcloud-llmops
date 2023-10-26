package com.starcloud.ops.business.app.convert.conversation;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.chat.config.dto.ApiSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.AppWorkflowSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.BaseExpandConfigDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.ChatMenuConfigDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.GptPluginSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.dto.SystemHandlerSkillDTO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigReqVO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigRespVO;
import com.starcloud.ops.business.app.dal.databoject.config.ChatExpandConfigDO;
import com.starcloud.ops.business.app.domain.entity.config.OpenaiCompletionParams;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkill;
import com.starcloud.ops.business.app.domain.entity.skill.HandlerSkill;
import com.starcloud.ops.business.app.enums.config.ChatExpandConfigEnum;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CHAT_CONFIG_TYPE_ERROR;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.CONFIG_ERROR;

/**
 * @author starcloud
 */
@Mapper
public interface ChatConfigConvert {

    ChatConfigConvert INSTANCE = Mappers.getMapper(ChatConfigConvert.class);

    ChatOpenAI toOpenaiModel(OpenaiCompletionParams params);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateParams(OpenaiCompletionParams params, @MappingTarget ChatOpenAI chatOpenAi);

    default List<ChatExpandConfigRespVO> convert(List<ChatExpandConfigDO> chatExpandConfigs) {
        if (CollectionUtils.isEmpty(chatExpandConfigs)) {
            return Collections.emptyList();
        }

        List<ChatExpandConfigRespVO> result = new ArrayList<>(chatExpandConfigs.size());
        for (ChatExpandConfigDO chatExpandConfig : chatExpandConfigs) {
            ChatExpandConfigRespVO respVO = convertConfig(chatExpandConfig);
            result.add(respVO);
        }
        return result;
    }


    default ChatExpandConfigRespVO convertConfig(ChatExpandConfigDO chatExpandConfig) {
        ChatExpandConfigRespVO respVO = convert(chatExpandConfig);
        ChatExpandConfigEnum configType = ChatExpandConfigEnum.getMenu(chatExpandConfig.getType());
        if (configType == null) {
            return respVO;
        }
        switch (configType) {
            case MENU:
                respVO.setChatMenuConfigDTO(JSONUtil.toBean(chatExpandConfig.getConfig(), ChatMenuConfigDTO.class));
                break;
            case API:
                respVO.setApiSkillDTO(JSONUtil.toBean(chatExpandConfig.getConfig(), ApiSkillDTO.class));
                break;
            case APP_WORKFLOW:
                respVO.setAppWorkflowSkillDTO(JSONUtil.toBean(chatExpandConfig.getConfig(), AppWorkflowSkillDTO.class));
                break;
            case GPT_PLUG:
                respVO.setGptPluginSkillDTO(JSONUtil.toBean(chatExpandConfig.getConfig(), GptPluginSkillDTO.class));
                break;
            case SYSTEM_HANDLER:
                respVO.setSystemHandlerSkillDTO(JSONUtil.toBean(chatExpandConfig.getConfig(), SystemHandlerSkillDTO.class));
                break;
            default:
                throw exception(CHAT_CONFIG_TYPE_ERROR, configType.getDescription());
        }
        return respVO;
    }

    ChatExpandConfigRespVO convert(ChatExpandConfigDO chatExpandConfig);


    default ChatExpandConfigDO convert(ChatExpandConfigReqVO reqVO) {
        ChatExpandConfigDO chatExpandConfigDO = new ChatExpandConfigDO();

        ChatExpandConfigEnum menu = ChatExpandConfigEnum.getMenu(reqVO.getType());
        switch (Objects.requireNonNull(menu)) {
            case API:
                check(reqVO.getApiSkillDTO(), menu.getDescription());
                chatExpandConfigDO.setConfig(JSONUtil.toJsonStr(reqVO.getApiSkillDTO()));
                break;
            case MENU:
                check(reqVO.getChatMenuConfigDTO(), menu.getDescription());
                chatExpandConfigDO.setConfig(JSONUtil.toJsonStr(reqVO.getChatMenuConfigDTO()));
                break;
            case APP_WORKFLOW:
                check(reqVO.getAppWorkflowSkillDTO(), menu.getDescription());
                chatExpandConfigDO.setConfig(JSONUtil.toJsonStr(reqVO.getAppWorkflowSkillDTO()));
                break;
            case GPT_PLUG:
                check(reqVO.getGptPluginSkillDTO(), menu.getDescription());
                chatExpandConfigDO.setConfig(JSONUtil.toJsonStr(reqVO.getGptPluginSkillDTO()));
                break;
            case SYSTEM_HANDLER:
                check(reqVO.getSystemHandlerSkillDTO(), menu.getDescription());
                chatExpandConfigDO.setConfig(JSONUtil.toJsonStr(reqVO.getSystemHandlerSkillDTO()));
                break;
            default:
                throw exception(CHAT_CONFIG_TYPE_ERROR, menu.getDescription());
        }

        chatExpandConfigDO.setAppConfigId(reqVO.getAppConfigId());
        if (StringUtils.isBlank(reqVO.getUid())) {
            reqVO.setUid(IdUtil.fastSimpleUUID());
        }
        chatExpandConfigDO.setUid(reqVO.getUid());
        chatExpandConfigDO.setType(reqVO.getType());
        chatExpandConfigDO.setDisabled(reqVO.getDisabled());
        return chatExpandConfigDO;
    }

    default void check(BaseExpandConfigDTO baseExpandConfigDTO, String desc) {
        if (baseExpandConfigDTO == null) {
            throw exception(CONFIG_ERROR, desc);
        }
        baseExpandConfigDTO.valid();
    }

    @Mapping(source = "copyWriting", target = "tips")
    HandlerSkill convert(SystemHandlerSkillDTO systemHandlerSkillDTO);

    AppWorkflowSkill convert(AppWorkflowSkillDTO appWorkflowSkillDTO);

}
