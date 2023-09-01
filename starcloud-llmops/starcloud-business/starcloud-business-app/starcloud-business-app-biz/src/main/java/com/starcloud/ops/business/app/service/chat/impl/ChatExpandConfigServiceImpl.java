package com.starcloud.ops.business.app.service.chat.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.chat.config.dto.BaseExpandConfigDTO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigReqVO;
import com.starcloud.ops.business.app.api.chat.config.vo.ChatExpandConfigRespVO;
import com.starcloud.ops.business.app.convert.conversation.ChatConfigConvert;
import com.starcloud.ops.business.app.dal.databoject.config.ChatExpandConfigDO;
import com.starcloud.ops.business.app.dal.mysql.config.ChatExpandConfigMapper;
import com.starcloud.ops.business.app.enums.config.ChatExpandConfigEnum;
import com.starcloud.ops.business.app.service.chat.ChatExpandConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
    public String create(ChatExpandConfigReqVO reqVO) {
        if (StringUtils.isBlank(reqVO.getAppConfigId())) {
            reqVO.setAppConfigId(IdUtil.fastSimpleUUID());
        }

        ChatExpandConfigDO configDO = ChatConfigConvert.INSTANCE.convert(reqVO);
        chatExpandConfigMapper.insert(configDO);
        return configDO.getAppConfigId();
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
}
