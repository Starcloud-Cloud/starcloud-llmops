package com.starcloud.ops.business.app.dal.mysql.comment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.app.controller.admin.comment.vo.MediaCommentsPageReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 媒体评论 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface MediaCommentsMapper extends BaseMapperX<MediaCommentsDO> {

    default PageResult<MediaCommentsDO> selectPage(Long userId, MediaCommentsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MediaCommentsDO>()
                .eqIfPresent(MediaCommentsDO::getAccountType, reqVO.getAccountType())
                .eqIfPresent(MediaCommentsDO::getAccountCode, reqVO.getAccountCode())
                .likeIfPresent(MediaCommentsDO::getAccountName, reqVO.getAccountName())
                .eqIfPresent(MediaCommentsDO::getAccountAvatar, reqVO.getAccountAvatar())
                .eqIfPresent(MediaCommentsDO::getMediaTitle, reqVO.getMediaTitle())
                .eqIfPresent(MediaCommentsDO::getMediaCover, reqVO.getMediaCover())
                .eqIfPresent(MediaCommentsDO::getCommentUserCode, reqVO.getCommentUserCode())
                .likeIfPresent(MediaCommentsDO::getCommentUserName, reqVO.getCommentUserName())
                .eqIfPresent(MediaCommentsDO::getCommentUserAvatar, reqVO.getCommentUserAvatar())
                .eqIfPresent(MediaCommentsDO::getCommentContent, reqVO.getCommentContent())
                .eqIfPresent(MediaCommentsDO::getResponseStatus, reqVO.getResponseStatus())
                .eqIfPresent(MediaCommentsDO::getCommentCode, reqVO.getCommentCode())
                .eqIfPresent(MediaCommentsDO::getMediaCode, reqVO.getMediaCode())
                .eqIfPresent(MediaCommentsDO::getLikeStatus, reqVO.getLikeStatus())
                .eqIfPresent(MediaCommentsDO::getConcernStatus, reqVO.getConcernStatus())
                .betweenIfPresent(MediaCommentsDO::getCreateTime, reqVO.getCreateTime())
                .eq(MediaCommentsDO::getCreator, userId)
                .orderByDesc(MediaCommentsDO::getId));
    }


    default MediaCommentsDO selectOneByUserId(Long userId, Long commentId) {
        return selectOne(MediaCommentsDO::getCreator, userId, MediaCommentsDO::getId, commentId);
    }

}