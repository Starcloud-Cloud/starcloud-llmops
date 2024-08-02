package com.starcloud.ops.business.app.dal.mysql.comment;

import java.time.LocalTime;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.app.controller.admin.comment.vo.strategy.MediaStrategyPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaStrategyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 媒体回复策略 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MediaStrategyMapper extends BaseMapperX<MediaStrategyDO> {



    default MediaStrategyDO selectOneByUserId(Long userId,Long strategyId) {
        return selectOne(MediaStrategyDO::getCreator, userId, MediaStrategyDO::getId, strategyId);
    }


    default PageResult<MediaStrategyDO> selectPage(Long userId,MediaStrategyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MediaStrategyDO>()
                .likeIfPresent(MediaStrategyDO::getName, reqVO.getName())
                .eqIfPresent(MediaStrategyDO::getPlatformType, reqVO.getPlatformType())
                .eqIfPresent(MediaStrategyDO::getActionType, reqVO.getStrategyType())
                .eqIfPresent(MediaStrategyDO::getKeywordMatchType, reqVO.getKeywordMatchType())
                .eqIfPresent(MediaStrategyDO::getKeywordGroups, reqVO.getKeywordGroups())
                .eqIfPresent(MediaStrategyDO::getActions, reqVO.getActions())
                .eqIfPresent(MediaStrategyDO::getIntervalTimes, reqVO.getIntervalTimes())
                .eqIfPresent(MediaStrategyDO::getFrequency, reqVO.getFrequency())
                .eqIfPresent(MediaStrategyDO::getAssignAccount, reqVO.getAssignAccount())
                .eqIfPresent(MediaStrategyDO::getAssignMedia, reqVO.getAssignMedia())
                .betweenIfPresent(MediaStrategyDO::getValidStartTime, reqVO.getValidStartTime())
                .betweenIfPresent(MediaStrategyDO::getValidEndTime, reqVO.getValidEndTime())
                .eqIfPresent(MediaStrategyDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MediaStrategyDO::getCreateTime, reqVO.getCreateTime())
                .eq(MediaStrategyDO::getCreateTime, userId)
                .orderByDesc(MediaStrategyDO::getId));
    }

    default MediaStrategyDO selectOneStatusEnableParams(Long userId, String accountCode, String mediaCode, LocalTime now, Integer status) {
        return selectOne(new LambdaQueryWrapperX<MediaStrategyDO>()
                .eqIfPresent(MediaStrategyDO::getCreator, userId)
                .eqIfPresent(MediaStrategyDO::getAssignAccount, accountCode)
                .eqIfPresent(MediaStrategyDO::getAssignMedia, mediaCode)
                .geIfPresent(MediaStrategyDO::getValidStartTime, now)
                .leIfPresent(MediaStrategyDO::getValidEndTime, now)
                .eqIfPresent(MediaStrategyDO::getStatus, status)
        );

    }
}