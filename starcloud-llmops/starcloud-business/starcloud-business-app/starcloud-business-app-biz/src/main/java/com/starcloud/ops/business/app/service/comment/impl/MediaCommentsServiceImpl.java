package com.starcloud.ops.business.app.service.comment.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsPageReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsActionMapper;
import com.starcloud.ops.business.app.dal.mysql.comment.MediaCommentsMapper;
import com.starcloud.ops.business.app.service.comment.MediaCommentsService;
import com.starcloud.ops.business.app.service.comment.MediaStrategyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
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
        mediaStrategyService.validateMediaCommentsMatch(userId, mediaComments.getId(), mediaComments.getAccountCode(), mediaComments.getMediaCode(),mediaComments.getCommentUserCode(), mediaComments.getCommentCode(), mediaComments.getCommentContent());
        // 返回
        return mediaComments.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMediaComments(MediaCommentsSaveReqVO updateReqVO) {
        // 校验存在
        validateMediaCommentsExists(updateReqVO.getId());
        // 更新
        MediaCommentsDO updateObj = BeanUtils.toBean(updateReqVO, MediaCommentsDO.class);
        mediaCommentsMapper.updateById(updateObj);

        // // 更新子表
        // updateMediaCommentsActionList(updateReqVO.getId(), updateReqVO.getMediaCommentsActions());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMediaComments(Long id) {
        // 校验存在
        validateMediaCommentsExists(id);
        // 删除
        mediaCommentsMapper.deleteById(id);

        // 删除子表
        deleteMediaCommentsActionByCommentsId(id);
    }

    private void validateMediaCommentsExists(Long id) {
        if (mediaCommentsMapper.selectById(id) == null) {
            throw exception(MEDIA_COMMENTS_NOT_EXISTS);
        }
    }

    @Override
    public MediaCommentsDO getMediaComments(Long id) {
        return mediaCommentsMapper.selectById(id);
    }

    @Override
    public PageResult<MediaCommentsDO> getMediaCommentsPage(MediaCommentsPageReqVO pageReqVO) {
        return mediaCommentsMapper.selectPage(pageReqVO);
    }

    // ==================== 子表（媒体评论操作） ====================

    @Override
    public List<MediaCommentsActionDO> getMediaCommentsActionListByCommentsId(Long commentsId) {
        return mediaCommentsActionMapper.selectListByCommentsId(commentsId);
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