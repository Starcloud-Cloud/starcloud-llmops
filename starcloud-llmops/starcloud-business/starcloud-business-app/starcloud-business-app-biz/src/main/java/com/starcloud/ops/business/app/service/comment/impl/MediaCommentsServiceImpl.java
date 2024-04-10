package com.starcloud.ops.business.app.service.comment.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.comment.vo.comment.*;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsMapper;
import com.starcloud.ops.business.app.enums.comment.ActionStatusEnum;
import com.starcloud.ops.business.app.enums.comment.ActionTypeEnum;
import com.starcloud.ops.business.app.enums.comment.ExecuteTypeEnum;
import com.starcloud.ops.business.app.service.comment.MediaCommentsActionService;
import com.starcloud.ops.business.app.service.comment.MediaCommentsService;
import com.starcloud.ops.business.app.service.comment.MediaStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MEDIA_COMMENTS_NOT_EXISTS;

/**
 * 媒体评论 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class MediaCommentsServiceImpl implements MediaCommentsService {

    @Resource
    private MediaStrategyService mediaStrategyService;

    @Resource
    private MediaCommentsActionService mediaCommentsActionService;

    @Resource
    private MediaCommentsMapper mediaCommentsMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMediaComments(Long userId, MediaCommentsSaveReqVO createReqVO) {
        // 插入
        MediaCommentsDO mediaComments = BeanUtils.toBean(createReqVO, MediaCommentsDO.class);
        mediaComments.setResponseStatus(ActionStatusEnum.NO_EXECUTION.getCode());
        mediaComments.setLikeStatus(ActionStatusEnum.NO_EXECUTION.getCode());
        mediaComments.setConcernStatus(ActionStatusEnum.NO_EXECUTION.getCode());

        // 评论幂等性验证
        MediaCommentsDO mediaCommentsDO = validateMediaCommentsExists(userId, createReqVO.getAccountType(), createReqVO.getCommentCode());
        if (mediaCommentsDO!=null){
            log.warn("【已存在评论数据，当前评论{}已经存在】", mediaCommentsDO);
            return mediaCommentsDO.getId();
        }

        mediaCommentsMapper.insert(mediaComments);

        // 异步处理评论
        mediaStrategyService.validateMediaCommentsMatch(userId, mediaComments.getId(), mediaComments.getAccountCode(), mediaComments.getMediaCode(), mediaComments.getCommentUserCode(), mediaComments.getCommentCode(), mediaComments.getCommentContent());
        // 返回
        return mediaComments.getId();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMediaComments(Long userId, MediaCommentsUpdateReqVO updateReqVO) {
        // 校验存在
        validateMediaCommentsExists(userId, updateReqVO.getId());
        // 更新
        MediaCommentsDO updateObj = BeanUtils.toBean(updateReqVO, MediaCommentsDO.class);
        mediaCommentsMapper.updateById(updateObj);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMediaComments(Long userId, Long id) {
        // 校验存在
        validateMediaCommentsExists(userId, id);
        // 删除
        mediaCommentsMapper.deleteById(id);

        // 删除子表
        mediaCommentsActionService.deleteByCommentsId(id);
    }

    private void validateMediaCommentsExists(Long userId, Long id) {
        if (mediaCommentsMapper.selectOneByUserId(userId, id) == null) {
            throw exception(MEDIA_COMMENTS_NOT_EXISTS);
        }
    }

    @Override
    public MediaCommentsDO getMediaComments(Long userId, Long id) {
        return mediaCommentsMapper.selectOneByUserId(userId, id);
    }

    @Override
    public PageResult<MediaCommentsDO> getMediaCommentsPage(Long userId, MediaCommentsPageReqVO pageReqVO) {
        return mediaCommentsMapper.selectPage(userId, pageReqVO);
    }

    // ==================== 子表（媒体评论操作） ====================

    @Override
    public List<MediaCommentsActionDO> getMediaCommentsActionListByCommentsId(Long commentsId) {
        return mediaCommentsActionService.selectListByCommentsId(commentsId);
    }

    /**
     * @param commentsId                  评论编号
     * @param actionType                  操作类型
     * @param estimatedExecutionStartTime 预计执行时间范围的开始时间
     * @param estimatedExecutionEndTime   预计执行时间范围的结束时间
     * @return 操作列表
     */
    @Override
    public List<MediaCommentsActionDO> getMediaCommentsActionListByCommentsId(Long commentsId, Integer actionType, LocalDateTime estimatedExecutionStartTime, LocalDateTime estimatedExecutionEndTime) {
        return mediaCommentsActionService.selectListByCommentsId(commentsId, actionType, estimatedExecutionStartTime, estimatedExecutionEndTime);
    }

    /**
     * 更新操作执行状态
     *
     * @param userId 用户编号
     * @param reqVO  操作 VO
     */
    @Override
    @Transactional
    public void updateActionSendStatus(Long userId, MediaCommentsActionReqVO reqVO) {
        // 验证评论是否存在
        validateMediaCommentsExists(userId, reqVO.getId());

        MediaCommentsActionDO mediaCommentsAction = mediaCommentsActionService.getMediaCommentsAction(reqVO.getId());
        ActionTypeEnum actionType = ActionTypeEnum.getByCode(mediaCommentsAction.getActionType());

        MediaCommentsDO.MediaCommentsDOBuilder builder = MediaCommentsDO.builder();
        builder.id(reqVO.getId());
        switch (actionType) {
            case RESPONSE:
                builder.responseStatus(reqVO.getActionExecuteType());
                break;
            case LIKE:
                builder.likeStatus(reqVO.getActionExecuteType());
                break;
            case CONCERN:
                builder.concernStatus(reqVO.getActionExecuteType());
        }

        mediaCommentsMapper.updateById(builder.build());

        // 更新操作
        mediaCommentsActionService.updateMediaCommentsActionStatus(reqVO.getId(), reqVO.getActionId(), reqVO.getActionExecuteType(), reqVO.getExecuteTime());
    }

    /**
     * 手动回复评论
     *
     * @param userId          用户编号
     * @param id              评论编号
     * @param responseContent 回复内容
     */
    @Override
    public void manualResponseMediaComments(Long userId, Long id, String responseContent) {
        // 校验存在
        MediaCommentsDO mediaCommentsDO = mediaCommentsMapper.selectOneByUserId(userId, id);
        if (mediaCommentsDO == null) {
            throw exception(MEDIA_COMMENTS_NOT_EXISTS);
        }

        mediaCommentsMapper.updateById(MediaCommentsDO.builder().id(mediaCommentsDO.getId()).responseStatus(ActionStatusEnum.MANUAL.getCode()).build());
        // 存入操作表
        mediaCommentsActionService.createMediaCommentsAction(userId, mediaCommentsDO.getCommentUserCode(), id, null, ActionTypeEnum.RESPONSE.getCode(), ExecuteTypeEnum.AUTO.getCode(), responseContent, 0L);
    }

    /**
     * 更新策略命中结果
     *
     * @param commentsId       评论编号
     * @param actionStatusEnum 策略匹配类型
     * @param actionTypeCode   操作类型
     * @param id               策略编号
     */
    @Override
    public void updateCommentStrategyResult(Long commentsId, ActionStatusEnum actionStatusEnum, Integer actionTypeCode, Long id) {

        MediaCommentsDO mediaCommentsDO = new MediaCommentsDO();
        mediaCommentsDO.setId(commentsId);

        ActionTypeEnum actionType = ActionTypeEnum.getByCode(actionTypeCode);

        switch (actionType) {
            case RESPONSE:
                mediaCommentsDO.setResponseStatus(actionStatusEnum.getCode());
                mediaCommentsDO.setResponseStrategyId(commentsId);
            case LIKE:
                mediaCommentsDO.setLikeStatus(actionStatusEnum.getCode());
                mediaCommentsDO.setLikeStrategyId(commentsId);
            case CONCERN:
                mediaCommentsDO.setConcernStatus(actionStatusEnum.getCode());
                mediaCommentsDO.setConcernStrategyId(commentsId);
        }

        // 更新数据
        mediaCommentsMapper.updateById(mediaCommentsDO);
    }

    /**
     * @param userId 用户编号
     * @param reqVO  列表 VO
     * @return 列表
     */
    @Override
    public List<MediaCommentsDO> getMatchSuccessList(Long userId, MediaCommentsListReqVO reqVO) {
        return null;
    }

    // private void createMediaCommentsActionList(Long commentsId, List<MediaCommentsActionDO> list) {
    //     list.forEach(o -> o.setCommentsId(commentsId));
    //     mediaCommentsActionMapper.insertBatch(list);
    // }
    //
    // private void updateMediaCommentsActionList(Long commentsId, List<MediaCommentsActionDO> list) {
    //     deleteMediaCommentsActionByCommentsId(commentsId);
    //     list.forEach(o -> o.setId(null).setUpdater(null).setUpdateTime(null)); // 解决更新情况下：1）id 冲突；2）updateTime 不更新
    //     createMediaCommentsActionList(commentsId, list);
    // }

    // private void deleteMediaCommentsActionByCommentsId(Long commentsId) {

    // }
    /**
     * 评论幂等性处理
     *
     * @param userId      用户编号
     * @param accountType 账号类型
     * @param commentCode 评论编号
     */
    private MediaCommentsDO validateMediaCommentsExists(Long userId, Integer accountType, String commentCode) {
        return   mediaCommentsMapper.selectOneByCommentCode(userId,accountType,commentCode);

    }

}