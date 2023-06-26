package com.starcloud.ops.business.log.api;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationUpdateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogAppApiImpl implements LogAppApi {

    @Autowired
    private LogAppConversationService logAppConversationService;

    @Autowired
    private LogAppMessageService logAppMessageService;

    @Override
    public LogAppConversationCreateReqVO createAppConversation(LogAppConversationCreateReqVO logAppConversationCreateReqVO) {

        if (StrUtil.isBlank(logAppConversationCreateReqVO.getUid())) {

            logAppConversationCreateReqVO.setUid(IdUtil.fastSimpleUUID());

            logAppConversationService.createAppConversation(logAppConversationCreateReqVO);

        } else {

            LogAppConversationDO logAppConversationDO = logAppConversationService.getAppConversation(logAppConversationCreateReqVO.getUid());

            if (logAppConversationDO == null) {

                logAppConversationService.createAppConversation(logAppConversationCreateReqVO);
            }
        }

        return logAppConversationCreateReqVO;
    }

    @Override
    public void updateAppConversationStatus(String uid, LogStatusEnum statusEnum) {

        LogAppConversationUpdateReqVO updateReqVO = new LogAppConversationUpdateReqVO();

        updateReqVO.setUid(uid);
        updateReqVO.setStatus(statusEnum.name());
        logAppConversationService.updateAppConversation(updateReqVO);
    }


    @Override
    public void createAppMessage(LogAppMessageCreateReqVO logAppMessageCreateReqVO) {

        logAppMessageService.createAppMessage(logAppMessageCreateReqVO);
    }


    @Override
    public LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid) {

        LogAppMessageDO logAppMessageDO = logAppMessageService.getAppMessage(appMessageUid);

        Assert.notNull(logAppMessageDO, "appMessageResult is not found");

        LogAppMessageInfoRespVO info = LogAppMessageConvert.INSTANCE.convertInfo(logAppMessageDO);

        return info;
    }

}
