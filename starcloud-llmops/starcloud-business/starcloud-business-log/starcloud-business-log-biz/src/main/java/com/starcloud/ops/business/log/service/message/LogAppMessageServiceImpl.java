package com.starcloud.ops.business.log.service.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.log.api.message.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;

import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.*;

/**
 * 应用执行日志结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class LogAppMessageServiceImpl implements LogAppMessageService {

    @Resource
    private LogAppMessageMapper appMessageMapper;

    @Override
    public Long createAppMessage(LogAppMessageCreateReqVO createReqVO) {
        // 插入
        LogAppMessageDO appMessage = LogAppMessageConvert.INSTANCE.convert(createReqVO);

        appMessage.setCreator(createReqVO.getCreator());
        appMessage.setUpdater(createReqVO.getCreator());

        appMessage.setCreateTime(createReqVO.getCreateTime());
        appMessage.setUpdateTime(createReqVO.getUpdateTime());

        appMessage.setTenantId(createReqVO.getTenantId());

        appMessageMapper.insert(appMessage);
        // 返回
        return appMessage.getId();
    }

    @Override
    public void updateAppMessage(LogAppMessageUpdateReqVO updateReqVO) {
        // 校验存在
        validateAppMessageExists(updateReqVO.getId());
        // 更新
        LogAppMessageDO updateObj = LogAppMessageConvert.INSTANCE.convert(updateReqVO);
        appMessageMapper.updateById(updateObj);
    }

    @Override
    public void deleteAppMessage(Long id) {
        // 校验存在
        validateAppMessageExists(id);
        // 删除
        appMessageMapper.deleteById(id);
    }

    private void validateAppMessageExists(Long id) {
        if (appMessageMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }

    @Override
    public LogAppMessageDO getAppMessage(Long id) {
        return appMessageMapper.selectById(id);
    }

    @Override
    public LogAppMessageDO getAppMessage(String uid) {
        return appMessageMapper.selectOne("uid", uid);
    }

    @Override
    public List<LogAppMessageDO> getAppMessageList(Collection<Long> ids) {
        return appMessageMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<LogAppMessageDO> getAppMessagePage(LogAppMessagePageReqVO pageReqVO) {
        return appMessageMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<LogAppMessageDO> userMessagePage(LogAppMessagePageReqVO reqVO) {
        return appMessageMapper.selectPage(reqVO, new LambdaQueryWrapperX<LogAppMessageDO>()
                .eqIfPresent(LogAppMessageDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppMessageDO::getAppConversationUid, reqVO.getAppConversationUid())
                .eqIfPresent(LogAppMessageDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppMessageDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppMessageDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppMessageDO::getAppStep, reqVO.getAppStep())
                .eqIfPresent(LogAppMessageDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppMessageDO::getErrorCode, reqVO.getErrorCode())
                .eqIfPresent(LogAppMessageDO::getErrorMsg, reqVO.getErrorMsg())
                .eqIfPresent(LogAppMessageDO::getVariables, reqVO.getVariables())
                .eqIfPresent(LogAppMessageDO::getMessage, reqVO.getMessage())
                .eqIfPresent(LogAppMessageDO::getMessageTokens, reqVO.getMessageTokens())
                .eqIfPresent(LogAppMessageDO::getMessageUnitPrice, reqVO.getMessageUnitPrice())
                .eqIfPresent(LogAppMessageDO::getAnswer, reqVO.getAnswer())
                .eqIfPresent(LogAppMessageDO::getAnswerTokens, reqVO.getAnswerTokens())
                .eqIfPresent(LogAppMessageDO::getAnswerUnitPrice, reqVO.getAnswerUnitPrice())
                .eqIfPresent(LogAppMessageDO::getElapsed, reqVO.getElapsed())
                .eqIfPresent(LogAppMessageDO::getTotalPrice, reqVO.getTotalPrice())
                .eqIfPresent(LogAppMessageDO::getCurrency, reqVO.getCurrency())
                .eqIfPresent(LogAppMessageDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppMessageDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppMessageDO::getCreateTime, reqVO.getCreateTime())
                .ne(LogAppMessageDO::getFromScene,"SYSTEM_SUMMARY")
                .orderByDesc(LogAppMessageDO::getId));
    }

    @Override
    public List<LogAppMessageDO> getAppMessageList(LogAppMessageExportReqVO exportReqVO) {
        return appMessageMapper.selectList(exportReqVO);
    }

}