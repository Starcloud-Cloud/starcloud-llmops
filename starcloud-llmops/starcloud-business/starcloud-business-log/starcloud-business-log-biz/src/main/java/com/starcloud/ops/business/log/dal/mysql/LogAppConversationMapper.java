package com.starcloud.ops.business.log.dal.mysql;

import java.util.*;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.method.mp.SelectCount;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.starcloud.ops.business.core.mybatis.query.MPJLambdaWrapperX;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationExportReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.*;
import org.apache.ibatis.annotations.Mapper;


/**
 * 应用执行日志会话 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LogAppConversationMapper extends BaseMapperX<LogAppConversationDO> {

    default PageResult<LogAppConversationDO> selectPage(LogAppConversationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppName, reqVO.getAppName())
                .eqIfPresent(LogAppConversationDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppConversationDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppConversationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppConversationDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppConversationDO::getId));
    }

    default List<LogAppConversationDO> selectList(LogAppConversationExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<LogAppConversationDO>()
                .eqIfPresent(LogAppConversationDO::getUid, reqVO.getUid())
                .eqIfPresent(LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eqIfPresent(LogAppConversationDO::getAppName, reqVO.getAppName())
                .eqIfPresent(LogAppConversationDO::getAppMode, reqVO.getAppMode())
                .eqIfPresent(LogAppConversationDO::getAppConfig, reqVO.getAppConfig())
                .eqIfPresent(LogAppConversationDO::getStatus, reqVO.getStatus())
                .eqIfPresent(LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eqIfPresent(LogAppConversationDO::getEndUser, reqVO.getEndUser())
                .betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LogAppConversationDO::getId));
    }


    default PageResult<LogAppConversationInfoPO> selectPage(LogAppConversationInfoPageReqVO reqVO) {

        MPJLambdaWrapperX<LogAppConversationDO> lambdaWrapperX = (MPJLambdaWrapperX<LogAppConversationDO>) new MPJLambdaWrapperX<LogAppConversationDO>()

                .select(LogAppConversationDO::getUid, LogAppConversationDO::getAppUid, LogAppConversationDO::getAppName, LogAppConversationDO::getAppMode, LogAppConversationDO::getFromScene,
                        LogAppConversationDO::getStatus, LogAppConversationDO::getCreator, LogAppConversationDO::getEndUser, LogAppConversationDO::getCreateTime)

                .selectCount(LogAppMessageDO::getId, LogAppConversationInfoPO::getMessageCount)
                .selectCount(LogAppMessageFeedbacksDO::getId, LogAppConversationInfoPO::getFeedbacksCount)
                .selectSum(LogAppMessageDO::getElapsed, LogAppConversationInfoPO::getTotalElapsed)
                .selectSum(LogAppMessageDO::getTotalPrice, LogAppConversationInfoPO::getTotalPrice)

                .selectSum(LogAppMessageDO::getMessageTokens, LogAppConversationInfoPO::getTotalMessageTokens)
                .selectSum(LogAppMessageDO::getAnswerTokens, LogAppConversationInfoPO::getTotalAnswerTokens)

                .leftJoin(LogAppMessageDO.class, LogAppMessageDO::getAppConversationUid, LogAppConversationDO::getUid)
                .leftJoin(LogAppMessageFeedbacksDO.class, LogAppMessageFeedbacksDO::getAppMessageUid, LogAppMessageDO::getUid)

                .eq(ObjectUtil.isNotEmpty(reqVO.getAppUid()), LogAppConversationDO::getAppUid, reqVO.getAppUid())
                .eq(ObjectUtil.isNotEmpty(reqVO.getAppName()), LogAppConversationDO::getAppName, reqVO.getAppName())

                .eq(ObjectUtil.isNotEmpty(reqVO.getStatus()), LogAppConversationDO::getStatus, reqVO.getStatus())
                .eq(ObjectUtil.isNotEmpty(reqVO.getFromScene()), LogAppConversationDO::getFromScene, reqVO.getFromScene())
                .eq(ObjectUtil.isNotEmpty(reqVO.getUser()), LogAppConversationDO::getCreator, reqVO.getUser())
                .eq(ObjectUtil.isNotEmpty(reqVO.getEndUser()), LogAppConversationDO::getEndUser, reqVO.getEndUser());

        lambdaWrapperX.betweenIfPresent(LogAppConversationDO::getCreateTime, reqVO.getStartTime(), reqVO.getEndTime())
                .orderByDesc(LogAppConversationDO::getId)
                .groupBy(LogAppConversationDO::getId);

        IPage<LogAppConversationInfoPO> mpPage = this.selectJoinPage(new Page<>(reqVO.getPageNo(), reqVO.getPageSize()), LogAppConversationInfoPO.class, lambdaWrapperX);


        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());

    }

}