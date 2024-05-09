package com.starcloud.ops.business.app.service.comment.impl;

import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsActionMapper;
import com.starcloud.ops.business.app.enums.comment.ActionStatusEnum;
import com.starcloud.ops.business.app.service.comment.MediaCommentsActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MEDIA_COMMENTS_ACTION_NOT_EXISTS;

/**
 * 媒体评论操作 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MediaCommentsActionServiceImpl implements MediaCommentsActionService {


    @Resource
    private MediaCommentsActionMapper mediaCommentsActionMapper;

    @Override
    public Long createMediaCommentsAction(MediaCommentsDO commentsDO) {
        // // 获取策略信息 在 mediaStrategyService中创建方法 方法的主要作用是 根据commentsDO.getCommentContent()中的数据 是否符合相应的规则 符合则返回规则 ID 和具体的回复内容 否则返回 null
        // // mediaStrategyService
        //
        // // MediaCommentsActionDO mediaCommentsAction = BeanUtils.toBean(createReqVO, MediaCommentsActionDO.class);
        // new MediaCommentsActionDO().setId().getCommentsId()
        // mediaCommentsActionMapper.insert(actionDO);
        // // 返回
        // return actionDO.getId();
        return null;
    }

    @Override
    public void updateMediaCommentsAction(MediaCommentsActionDO actionDO) {
        // 校验存在
        validateMediaCommentsActionExists(actionDO.getId());

        mediaCommentsActionMapper.updateById(actionDO);
    }

    @Override
    public void deleteMediaCommentsAction(Long id) {
        // 校验存在
        validateMediaCommentsActionExists(id);
        // 删除
        mediaCommentsActionMapper.deleteById(id);
    }


    @Override
    public MediaCommentsActionDO getMediaCommentsAction(Long id) {
        return mediaCommentsActionMapper.selectById(id);
    }

    /**
     * 获取单位时间内数据数量
     *
     * @param userId       用户编号
     * @param strategyType 策略类型
     * @param frequency    频次(小时)
     */
    @Override
    public int selectCountByStrategyAndPerUnitTime(Long userId, Integer strategyType, Integer frequency) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(frequency);
        return mediaCommentsActionMapper.selectCountByStrategyTypeAndFrequency(userId, strategyType, startTime, endTime);
    }


    /**
     * @param userId          用户编号
     * @param commentUserCode 评论人账户编号
     * @param commentsId      评论编号
     * @param strategyCode    策略编号
     * @param actionType      策略类型
     * @param executeType     执行类型
     * @param executeContent  执行内容
     * @param IntervalTimes   执行时间
     */
    @Override
    public void createMediaCommentsAction(Long userId, String commentUserCode, Long commentsId, Long strategyCode, Integer actionType, Integer executeType, String executeContent, Long IntervalTimes) {
        // 判断同一评论下是否存在相同的操作类型的数据
        if (mediaCommentsActionMapper.selectSameActionTypeAndCommentsId(commentsId, actionType) > 0) {
            log.warn("【评论操作数据添加失败，当前评论{}已经存在操作数据，该操作类型为{}", commentsId, actionType);
            return;
        }
        MediaCommentsActionDO mediaCommentsActionDO = new MediaCommentsActionDO();
        mediaCommentsActionDO.setCommentsId(commentsId);
        mediaCommentsActionDO.setStrategyId(strategyCode);
        mediaCommentsActionDO.setActionType(actionType);
        mediaCommentsActionDO.setExecuteType(executeType);
        mediaCommentsActionDO.setExecuteContent(executeContent);
        mediaCommentsActionDO.setExecuteObject(commentUserCode);
        mediaCommentsActionDO.setIntervalTimes(IntervalTimes);
        mediaCommentsActionMapper.insert(mediaCommentsActionDO);
    }

    /**
     * @param commentId 评论编号
     * @return MediaCommentsActionDO 列表
     */
    @Override
    public List<MediaCommentsActionDO> getActionListByCommentId(Long commentId) {
        return mediaCommentsActionMapper.selectListByCommentsId(commentId);
    }

    /**
     * 根据策略 ID 获取策略命中数
     *
     * @param strategyId 策略编号
     * @return 命中数量
     */
    @Override
    public Integer getCountByStrategyId(Long strategyId) {
        return mediaCommentsActionMapper.selectCountByStrategyId(strategyId);
    }

    /**
     * 根据评论编号获取操作列表
     *
     * @param commentsId 评论编号
     * @return 操作列表
     */
    @Override
    public List<MediaCommentsActionDO> selectListByCommentsId(Long commentsId) {
        return mediaCommentsActionMapper.selectListByCommentsId(commentsId, null, null, null);
    }

    /**
     * 根据评论编号获取操作列表
     *
     * @param commentsId                  评论编号
     * @param actionType                  操作类型
     * @param estimatedExecutionStartTime 预计执行开始时间
     * @param estimatedExecutionEndTime   预计执行结束时间
     * @return 操作列表
     */
    @Override
    public List<MediaCommentsActionDO> selectListByCommentsId(Long commentsId, Integer actionType, LocalDateTime estimatedExecutionStartTime, LocalDateTime estimatedExecutionEndTime) {
        return mediaCommentsActionMapper.selectListByCommentsId(commentsId, actionType, estimatedExecutionStartTime, estimatedExecutionEndTime);
    }

    /**
     * 通过评论编号删除评论下操作
     *
     * @param commentsId 评论编号
     */
    @Override
    public void deleteByCommentsId(Long commentsId) {
        mediaCommentsActionMapper.deleteByCommentsId(commentsId);
    }

    /**
     * 更新操作状态
     *
     * @param commentId         评论编号
     * @param actionId          操作编号
     * @param actionExecuteCode 操作执行状态类型
     * @param executeTime       执行时间
     */
    @Override
    public void updateMediaCommentsActionStatus(Long commentId, Long actionId, Integer actionExecuteCode, LocalDateTime executeTime) {

        MediaCommentsActionDO mediaCommentsActionDO = mediaCommentsActionMapper.selectById(actionId);
        if (mediaCommentsActionDO == null) {
            throw exception(MEDIA_COMMENTS_ACTION_NOT_EXISTS);
        }
        ActionStatusEnum actionExecuteType = ActionStatusEnum.getByCode(actionExecuteCode);

        MediaCommentsActionDO.MediaCommentsActionDOBuilder builder = MediaCommentsActionDO.builder();
        builder.id(actionId);

        switch (actionExecuteType) {
            case WAIT_SEND:
                builder.estimatedExecutionTime(executeTime);
                break;
            case SUCCESS_SEND:
                builder.actualExecutionTime(executeTime);
                break;
        }
        mediaCommentsActionMapper.updateById(builder.build());
    }


    private void validateMediaCommentsActionExists(Long id) {
        if (mediaCommentsActionMapper.selectById(id) == null) {
            throw exception(MEDIA_COMMENTS_ACTION_NOT_EXISTS);
        }
    }

}