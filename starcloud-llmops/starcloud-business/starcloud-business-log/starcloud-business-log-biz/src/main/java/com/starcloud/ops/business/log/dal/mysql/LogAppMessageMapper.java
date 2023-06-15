package com.starcloud.ops.business.log.dal.mysql;

import java.util.*;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.core.mybatis.query.MPJLambdaWrapperX;
import com.starcloud.ops.business.log.api.message.vo.*;
import com.starcloud.ops.business.log.dal.dataobject.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    List<LogAppMessageStatisticsListPO> getAppMessageStatisticsList(@Param("req") LogAppMessageStatisticsListReqVO reqVO);

}