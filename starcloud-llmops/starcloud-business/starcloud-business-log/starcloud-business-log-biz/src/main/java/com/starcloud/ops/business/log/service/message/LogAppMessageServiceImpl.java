package com.starcloud.ops.business.log.service.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageExportReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageUpdateReqVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.LogMessageTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS;

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

    /**
     * 创建应用执行日志结果
     *
     * @param request 创建信息
     * @return 编号
     */
    @Override
    public Long createAppMessage(LogAppMessageCreateReqVO request) {
        // 插入
        LogAppMessageDO appMessage = LogAppMessageConvert.INSTANCE.convert(request);

        appMessage.setCreator(request.getCreator());
        appMessage.setUpdater(request.getCreator());

        appMessage.setCreateTime(request.getCreateTime());
        appMessage.setUpdateTime(request.getUpdateTime());

        appMessage.setTenantId(request.getTenantId());

        appMessageMapper.insert(appMessage);
        // 返回
        return appMessage.getId();
    }

    /**
     * 更新应用执行日志结果
     *
     * @param request 更新信息
     */
    @Override
    public void updateAppMessage(LogAppMessageUpdateReqVO request) {
        // 校验存在
        validateAppMessageExists(request.getId());
        // 更新
        LogAppMessageDO updateObj = LogAppMessageConvert.INSTANCE.convert(request);
        appMessageMapper.updateById(updateObj);
    }

    /**
     * 删除应用执行日志结果
     *
     * @param id 编号
     */
    @Override
    public void deleteAppMessage(Long id) {
        // 校验存在
        validateAppMessageExists(id);
        // 删除
        appMessageMapper.deleteById(id);
    }

    /**
     * 获得应用执行日志结果
     *
     * @param id 编号
     * @return 应用执行日志结果
     */
    @Override
    public LogAppMessageDO getAppMessage(Long id) {
        return appMessageMapper.selectById(id);
    }

    /**
     * 获得应用执行日志结果
     *
     * @param uid 编号
     * @return 应用执行日志结果
     */
    @Override
    public LogAppMessageDO getAppMessage(String uid) {
        return appMessageMapper.selectOne("uid", uid);
    }

    /**
     * 根据会话uid获取消息列表
     *
     * @param query 查询条件
     * @return 消息列表
     */
    @Override
    public Page<LogAppMessageDO> getAppMessageList(AppLogMessagePageReqVO query) {
        LambdaQueryWrapper<LogAppMessageDO> wrapper = Wrappers.lambdaQuery(LogAppMessageDO.class);
        wrapper.eq(LogAppMessageDO::getAppConversationUid, query.getConversationUid());
        wrapper.eq(StringUtils.isNotBlank(query.getAppMode()), LogAppMessageDO::getAppMode, query.getAppMode());
        wrapper.eq(StringUtils.isNotBlank(query.getFromScene()), LogAppMessageDO::getFromScene, query.getFromScene());
        wrapper.eq(StringUtils.isNotBlank(query.getStatus()), LogAppMessageDO::getStatus, query.getStatus());
        wrapper.orderByDesc(LogAppMessageDO::getCreateTime);
        Page<LogAppMessageDO> page = new Page<>(query.getPageNo(), query.getPageSize());
        return appMessageMapper.selectPage(page, wrapper);
    }

    /**
     * 获得应用执行日志结果列表
     *
     * @param ids 编号
     * @return 应用执行日志结果列表
     */
    @Override
    public List<LogAppMessageDO> getAppMessageList(Collection<Long> ids) {
        return appMessageMapper.selectBatchIds(ids);
    }

    /**
     * 获得应用执行日志结果分页
     *
     * @param pageReqVO 分页查询
     * @return 应用执行日志结果分页
     */
    @Override
    public PageResult<LogAppMessageDO> getAppMessagePage(LogAppMessagePageReqVO pageReqVO) {
        return appMessageMapper.selectPage(pageReqVO);
    }

    /**
     * 排除系统总结场景
     *
     * @param reqVO 分页查询
     * @return 应用执行日志结果分页
     */
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
                .ne(LogAppMessageDO::getFromScene, "SYSTEM_SUMMARY")
                .ne(LogAppMessageDO::getMsgType, LogMessageTypeEnum.SUMMARY.name())
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 获得应用执行日志结果列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 应用执行日志结果列表
     */
    @Override
    public List<LogAppMessageDO> getAppMessageList(LogAppMessageExportReqVO exportReqVO) {
        return appMessageMapper.selectList(exportReqVO);
    }

    private void validateAppMessageExists(Long id) {
        if (appMessageMapper.selectById(id) == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS);
        }
    }

}