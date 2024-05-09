package com.starcloud.ops.business.app.service.comment;

import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.comment.vo.strategy.MediaStrategyPageReqVO;
import com.starcloud.ops.business.app.controller.admin.comment.vo.strategy.MediaStrategySaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaStrategyDO;

/**
 * 媒体回复策略 Service 接口
 *
 * @author starcloudadmin
 */
public interface MediaStrategyService {

    /**
     * 创建媒体回复策略
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMediaStrategy(Long userId, @Valid MediaStrategySaveReqVO createReqVO);

    /**
     * 更新媒体回复策略
     *
     * @param updateReqVO 更新信息
     */
    void updateMediaStrategy(Long userId, @Valid MediaStrategySaveReqVO updateReqVO);

    /**
     * 删除媒体回复策略
     *
     * @param id 编号
     */
    void deleteMediaStrategy(Long userId, Long id);

    /**
     * 获得媒体回复策略
     *
     * @param id 编号
     * @return 媒体回复策略
     */
    MediaStrategyDO getMediaStrategy(Long userId, Long id);

    /**
     * 获得媒体回复策略分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体回复策略分页
     */
    PageResult<MediaStrategyDO> getMediaStrategyPage(Long userId, MediaStrategyPageReqVO pageReqVO);

    /**
     * 验证 评论内容匹配
     *
     * @param userId          用户 ID
     * @param commentsId      评论 ID
     * @param accountCode     发布人账号 Code
     * @param mediaCode       媒体 Code
     * @param commentUserCode 评论人账号 Code
     * @param commentCode     评论 Code
     * @param commentContent  评论内容
     */
    void validateMediaCommentsMatch(Long userId, Long commentsId, String accountCode, String mediaCode, String commentUserCode, String commentCode, String commentContent);

    /**
     * 更新策略状态
     *
     * @param userId     用户编号
     * @param strategyId 策略编号
     * @param status     状态
     */
    void updateMediaStrategyStatus(Long userId, Long strategyId, Integer status);
}