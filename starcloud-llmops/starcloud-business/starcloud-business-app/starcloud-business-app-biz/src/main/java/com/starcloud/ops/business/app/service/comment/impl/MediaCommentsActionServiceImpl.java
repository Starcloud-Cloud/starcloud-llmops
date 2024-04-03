package com.starcloud.ops.business.app.service.comment.impl;

import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsActionMapper;
import com.starcloud.ops.business.app.service.comment.MediaCommentsActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
        // // 更新
        // MediaCommentsActionDO updateObj = BeanUtils.toBean(updateReqVO, MediaCommentsActionDO.class);
        mediaCommentsActionMapper.updateById(actionDO);
    }

    @Override
    public void deleteMediaCommentsAction(Long id) {
        // 校验存在
        validateMediaCommentsActionExists(id);
        // 删除
        mediaCommentsActionMapper.deleteById(id);
    }

    private void validateMediaCommentsActionExists(Long id) {
        if (mediaCommentsActionMapper.selectById(id) == null) {
            throw exception(MEDIA_COMMENTS_ACTION_NOT_EXISTS);
        }
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
     * @param executeTime     执行时间
     */
    @Override
    @Async
    public void createMediaCommentsAction(Long userId, String commentUserCode, Long commentsId, Long strategyCode, Integer actionType, Integer executeType, String executeContent, LocalDateTime executeTime) {
        // 判断同一评论下是否存在相同的操作类型的数据
        if (mediaCommentsActionMapper.selectSameActionTypeAndCommentsId(userId, commentsId, actionType) > 0) {
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
        mediaCommentsActionDO.setExecuteTime(executeTime);
    }

    /**
     * @param commentId  评论编号
     * @return MediaCommentsActionDO 列表
     */
    @Override
    public List<MediaCommentsActionDO> getActionListByCommentId(Long  commentId) {
        return mediaCommentsActionMapper.selectListByCommentsId(commentId);
    }
}