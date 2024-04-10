package com.starcloud.ops.business.app.service.comment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.comment.vo.comment.*;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.enums.comment.ActionStatusEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 媒体评论 Service 接口
 *
 * @author starcloudadmin
 */
public interface MediaCommentsService {

    /**
     * 创建媒体评论
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMediaComments(@NotNull Long userId, @Valid MediaCommentsSaveReqVO createReqVO);

    /**
     * 更新媒体评论
     *
     * @param updateReqVO 更新信息
     */
    void updateMediaComments(@NotNull Long userId, @Valid MediaCommentsUpdateReqVO updateReqVO);

    /**
     * 删除媒体评论
     *
     * @param id 编号
     */
    void deleteMediaComments(@NotNull Long userId, Long id);

    /**
     * 获得媒体评论
     *
     * @param id 编号
     * @return 媒体评论
     */
    MediaCommentsDO getMediaComments(@NotNull Long userId, Long id);

    /**
     * 获得媒体评论分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体评论分页
     */
    PageResult<MediaCommentsDO> getMediaCommentsPage(@NotNull Long userId, MediaCommentsPageReqVO pageReqVO);

    /**
     * 手动回复评论
     *
     * @param userId          用户编号
     * @param id              评论编号
     * @param responseContent 回复内容
     */
    void manualResponseMediaComments(Long userId, Long id, String responseContent);

    /**
     * 更新策略命中结果
     *
     * @param commentsId        评论编号
     * @param strategyMatchType 策略匹配类型
     * @param actionType        操作类型
     * @param id                策略编号
     */
    void updateCommentStrategyResult(Long commentsId, ActionStatusEnum strategyMatchType, Integer actionType, Long id);

    List<MediaCommentsDO> getMatchSuccessList(Long loginUserId, MediaCommentsListReqVO reqVO);


    // ==================== 子表（媒体评论操作） ====================

    /**
     * 获得媒体评论操作列表
     *
     * @param commentsId 评论编号
     * @return 媒体评论操作列表
     */
    List<MediaCommentsActionDO> getMediaCommentsActionListByCommentsId(Long commentsId);

    /**
     * @param commentsId                  评论编号
     * @param actionType                  操作类型
     * @param estimatedExecutionStartTime 预计执行时间范围的开始时间
     * @param estimatedExecutionEndTime   预计执行时间范围的结束时间
     * @return
     */
    List<MediaCommentsActionDO> getMediaCommentsActionListByCommentsId(Long commentsId, Integer actionType, LocalDateTime estimatedExecutionStartTime, LocalDateTime estimatedExecutionEndTime);

    /**
     * 更新操作执行状态
     * @param userId 用户编号
     * @param reqVO 操作 VO
     */
    void updateActionSendStatus(Long userId, MediaCommentsActionReqVO reqVO);
}