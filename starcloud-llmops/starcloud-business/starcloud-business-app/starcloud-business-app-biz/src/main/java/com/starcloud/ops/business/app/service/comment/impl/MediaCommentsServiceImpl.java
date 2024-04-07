package com.starcloud.ops.business.app.service.comment.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsPageReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsActionMapper;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsMapper;
import com.starcloud.ops.business.app.enums.comment.ActionTypeEnum;
import com.starcloud.ops.business.app.enums.comment.ExecuteTypeEnum;
import com.starcloud.ops.business.app.service.comment.MediaCommentsActionService;
import com.starcloud.ops.business.app.service.comment.MediaCommentsService;
import com.starcloud.ops.business.app.service.comment.MediaStrategyService;
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
@Service
@Validated
public class MediaCommentsServiceImpl implements MediaCommentsService {

    @Resource
    private MediaStrategyService mediaStrategyService;

    @Resource
    private MediaCommentsActionService mediaCommentsActionService;

    @Resource
    private MediaCommentsMapper mediaCommentsMapper;
    @Resource
    private MediaCommentsActionMapper mediaCommentsActionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMediaComments(Long userId, MediaCommentsSaveReqVO createReqVO) {
        // 插入
        MediaCommentsDO mediaComments = BeanUtils.toBean(createReqVO, MediaCommentsDO.class);
        mediaCommentsMapper.insert(mediaComments);
        // 异步处理评论
        mediaStrategyService.validateMediaCommentsMatch(userId, mediaComments.getId(), mediaComments.getAccountCode(), mediaComments.getMediaCode(), mediaComments.getCommentUserCode(), mediaComments.getCommentCode(), mediaComments.getCommentContent());
        // 返回
        return mediaComments.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMediaComments(Long userId, MediaCommentsSaveReqVO updateReqVO) {
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
        deleteMediaCommentsActionByCommentsId(id);
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
        return mediaCommentsActionMapper.selectListByCommentsId(commentsId);
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
        // 存入操作表
        mediaCommentsActionService.createMediaCommentsAction(userId, mediaCommentsDO.getCommentUserCode(), id, null, ActionTypeEnum.RESPONSE.getCode(), ExecuteTypeEnum.AUTO.getCode(), responseContent, LocalDateTime.now());
    }

    private void createMediaCommentsActionList(Long commentsId, List<MediaCommentsActionDO> list) {
        list.forEach(o -> o.setCommentsId(commentsId));
        mediaCommentsActionMapper.insertBatch(list);
    }

    private void updateMediaCommentsActionList(Long commentsId, List<MediaCommentsActionDO> list) {
        deleteMediaCommentsActionByCommentsId(commentsId);
        list.forEach(o -> o.setId(null).setUpdater(null).setUpdateTime(null)); // 解决更新情况下：1）id 冲突；2）updateTime 不更新
        createMediaCommentsActionList(commentsId, list);
    }

    private void deleteMediaCommentsActionByCommentsId(Long commentsId) {
        mediaCommentsActionMapper.deleteByCommentsId(commentsId);
    }

}