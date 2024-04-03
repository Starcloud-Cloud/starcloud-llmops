package com.starcloud.ops.business.app.dal.mysql.comment;

import java.time.LocalDateTime;
import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 媒体评论操作 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MediaCommentsActionMapper extends BaseMapperX<MediaCommentsActionDO> {

    default List<MediaCommentsActionDO> selectListByCommentsId(Long commentsId) {
        return selectList(MediaCommentsActionDO::getCommentsId, commentsId);
    }

    default int deleteByCommentsId(Long commentsId) {
        return delete(MediaCommentsActionDO::getCommentsId, commentsId);
    }

    default int selectCountByStrategyTypeAndFrequency(Long userId, Integer strategyType, LocalDateTime startTime, LocalDateTime endTime) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapper<>(MediaCommentsActionDO.class)
                .eq(MediaCommentsActionDO::getExecuteObject, userId)
                .eq(MediaCommentsActionDO::getActionType, strategyType)
                .ge(MediaCommentsActionDO::getCreateTime, startTime)
                .le(MediaCommentsActionDO::getCreateTime, endTime)));
    }

    default int selectSameActionTypeAndCommentsId(Long userId, Long commentsId, Integer actionType) {
        return Math.toIntExact(selectCount(new LambdaQueryWrapper<>(MediaCommentsActionDO.class)
                .eq(MediaCommentsActionDO::getExecuteObject, userId)
                .eq(MediaCommentsActionDO::getActionType, actionType)
                .eq(MediaCommentsActionDO::getCommentsId, commentsId)));
    }

}