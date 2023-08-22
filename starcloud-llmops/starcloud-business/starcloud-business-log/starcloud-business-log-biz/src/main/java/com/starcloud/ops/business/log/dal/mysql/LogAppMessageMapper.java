package com.starcloud.ops.business.log.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageExportReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListAppUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用执行日志结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppMessageMapper extends BaseMapperX<LogAppMessageDO> {

    default PageResult<LogAppMessageDO> selectPage(LogAppMessagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppMessageDO>()
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
                .orderByDesc(LogAppMessageDO::getId));
    }

    default List<LogAppMessageDO> selectList(LogAppMessageExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppMessageDO>()
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
                .orderByDesc(LogAppMessageDO::getId));
    }

    /**
     * 获得应用执行日志消息统计列表
     *
     * @param query 查询条件
     * @return 应用执行日志消息统计列表
     */
    List<LogAppMessageStatisticsListPO> listLogMessageStatistics(@Param("query") LogAppMessageStatisticsListReqVO query);

    /**
     * 根据应用 UID 获取应用执行日志消息统计数据列表
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    List<LogAppMessageStatisticsListPO> listLogMessageStatisticsByAppUid(@Param("query") LogAppMessageStatisticsListAppUidReqVO query);
}