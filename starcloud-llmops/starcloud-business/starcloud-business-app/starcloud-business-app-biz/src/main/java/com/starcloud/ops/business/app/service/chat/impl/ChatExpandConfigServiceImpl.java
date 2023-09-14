package com.starcloud.ops.business.app.service.chat.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.druid.util.StringUtils;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigReqVO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigRespVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.dal.databoject.config.ChatExpandConfigDO;
import com.starcloud.ops.business.app.dal.mysql.config.ChatExpandConfigMapper;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.ChatConfigEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.service.chat.ChatExpandConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

@Slf4j
@Service
public class ChatExpandConfigServiceImpl implements ChatExpandConfigService {

    @Resource
    private ChatExpandConfigMapper chatExpandConfigMapper;


    @Override
    public Map<Integer, List<ChatExpandConfigRespVO>> getConfig(String appConfigId) {
        List<ChatExpandConfigDO> expandConfigDOS = chatExpandConfigMapper.selectByAppConfigUid(appConfigId);
        List<ChatExpandConfigRespVO> respVOList = ChatConfigConvert.INSTANCE.convert(expandConfigDOS);
        return respVOList.stream().collect(Collectors.groupingBy(ChatExpandConfigRespVO::getType));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(ChatExpandConfigReqVO reqVO) {
        ChatAppEntity chatAppEntity = AppFactory.factoryChatApp(reqVO.getAppConfigId());
        if (chatAppEntity == null || chatAppEntity.getChatConfig() == null) {
            throw exception(APP_NO_EXISTS_UID, reqVO.getAppConfigId());
        }
        chatAppEntity.getChatConfig().setAppConfigId(reqVO.getAppConfigId());
        chatAppEntity.update();
        validExists(reqVO, chatAppEntity.getChatConfig());
        ChatExpandConfigDO configDO = ChatConfigConvert.INSTANCE.convert(reqVO);
        chatExpandConfigMapper.insert(configDO);
        return configDO.getAppConfigId();
    }

    private void validExists(ChatExpandConfigReqVO reqVO, ChatConfigEntity chatConfig) {
        if (reqVO.getSystemHandlerSkillDTO() != null && CollectionUtil.isNotEmpty(chatConfig.getHandlerSkills())) {
            String code = reqVO.getSystemHandlerSkillDTO().getCode();
            boolean match = chatConfig.getHandlerSkills().stream().anyMatch(handlerSkill -> StringUtils.equals(handlerSkill.getName(), code));
            if (match) {
                throw exception(CHAT_CONFIG_IS_REPEAT, "系统技能配置", code);
            }
        }
        if (reqVO.getAppWorkflowSkillDTO() != null && CollectionUtil.isNotEmpty(chatConfig.getAppWorkflowSkills())) {
            String appUid = reqVO.getAppWorkflowSkillDTO().getSkillAppUid();
            boolean match = chatConfig.getAppWorkflowSkills().stream().anyMatch(appWorkflowSkill -> StringUtils.equals(appWorkflowSkill.getSkillAppUid(), appUid));
            if (match) {
                throw exception(CHAT_CONFIG_IS_REPEAT, "应用技能配置", appUid);
            }
        }
    }

    @Override
    public void modify(ChatExpandConfigReqVO reqVO) {
        ChatExpandConfigDO chatExpandConfigDO = chatExpandConfigMapper.selectByUid(reqVO.getUid());
        if (chatExpandConfigDO == null) {
            throw exception(CHAT_CONFIG_NOT_EXIST);
        }
        if (!chatExpandConfigDO.getType().equals(reqVO.getType())) {
            throw exception(MODIFY_CONFIG_ERROR, chatExpandConfigDO.getUid(), chatExpandConfigDO.getType());
        }
        ChatExpandConfigDO configDO = ChatConfigConvert.INSTANCE.convert(reqVO);
        chatExpandConfigMapper.modify(configDO);
    }

    @Override
    public void delete(String uid) {
        ChatExpandConfigDO chatExpandConfigDO = chatExpandConfigMapper.selectByUid(uid);
        if (chatExpandConfigDO == null) {
            throw exception(CHAT_CONFIG_NOT_EXIST);
        }
        chatExpandConfigMapper.deleteById(chatExpandConfigDO.getId());
    }

    @Override
    public void copyConfig(String sourceConfigId, String targetConfigId) {
        List<ChatExpandConfigDO> sourceConfig = chatExpandConfigMapper.selectByAppConfigUid(sourceConfigId);
        sourceConfig = sourceConfig.stream().filter(chatExpandConfigDO -> !chatExpandConfigDO.getDisabled()).collect(Collectors.toList());
        List<ChatExpandConfigDO> targetConfig = chatExpandConfigMapper.selectByAppConfigUid(targetConfigId);

        for (ChatExpandConfigDO chatExpandConfigDO : targetConfig) {
            chatExpandConfigMapper.deleteById(chatExpandConfigDO);
        }

        for (ChatExpandConfigDO chatExpandConfigDO : sourceConfig) {

            ChatExpandConfigDO configDO = new ChatExpandConfigDO();
            configDO.setConfig(chatExpandConfigDO.getConfig());
            configDO.setType(chatExpandConfigDO.getType());
            configDO.setDisabled(false);
            configDO.setAppConfigId(targetConfigId);
            configDO.setUid(IdUtil.fastSimpleUUID());
            chatExpandConfigMapper.insert(configDO);
        }
    }
}
