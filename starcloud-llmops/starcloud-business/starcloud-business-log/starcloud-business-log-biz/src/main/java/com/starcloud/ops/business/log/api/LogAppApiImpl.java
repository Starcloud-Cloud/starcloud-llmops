package com.starcloud.ops.business.log.api;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author admin
 * @version 1.0.0
 * @since 2021-03-30
 */
@Slf4j
@Service
public class LogAppApiImpl implements LogAppApi {

    @Resource
    private LogAppConversationService logAppConversationService;

    @Resource
    private LogAppMessageService logAppMessageService;

    /**
     * 创建应用会话
     *
     * @param logAppConversationCreateReqVO 应用会话创建请求
     * @return 应用会话创建响应
     */
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

    /**
     * 更新应用会话状态
     *
     * @param uid        应用会话唯一标识
     * @param statusEnum 应用会话状态
     */
    @Override
    public void updateAppConversationStatus(String uid, LogStatusEnum statusEnum) {
        logAppConversationService.updateAppConversationStatus(uid, statusEnum.name());
    }

    /**
     * 更新应用会话状态
     *
     * @param request 应用会话状态更新请求
     */
    @Override
    public void updateAppConversationStatus(LogAppConversationStatusReqVO request) {
        logAppConversationService.updateAppConversationStatus(request);
    }

    /**
     * 创建日志应用消息
     *
     * @param logAppMessageCreateReqVO 日志应用消息创建请求
     */
    @Override
    public void createAppMessage(LogAppMessageCreateReqVO logAppMessageCreateReqVO) {
        logAppMessageService.createAppMessage(logAppMessageCreateReqVO);
    }

    /**
     * 获取应用消息结果
     *
     * @param appMessageUid 应用消息唯一标识
     * @return 应用消息结果
     */
    @Override
    public LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid) {
        LogAppMessageDO logAppMessageDO = logAppMessageService.getAppMessage(appMessageUid);
        Assert.notNull(logAppMessageDO, "appMessageResult is not found");
        return LogAppMessageConvert.INSTANCE.convertInfo(logAppMessageDO);
    }
}
