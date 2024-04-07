package com.starcloud.ops.business.app.service.comment;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.*;

import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsActionDO;
import com.starcloud.ops.business.app.dal.databoject.comment.MediaCommentsDO;

/**
 * 媒体评论操作 Service 接口
 *
 * @author starcloudadmin
 */
public interface MediaCommentsActionService {

    /**
     * 创建媒体评论操作
     *
     * @param commentsDO 创建信息
     * @return 编号
     */
    Long createMediaCommentsAction(@Valid MediaCommentsDO commentsDO);

    /**
     * 更新媒体评论操作
     *
     * @param actionDO 更新信息
     */
    void updateMediaCommentsAction(@Valid MediaCommentsActionDO actionDO);

    /**
     * 删除媒体评论操作
     *
     * @param id 编号
     */
    void deleteMediaCommentsAction(Long id);

    /**
     * 获得媒体评论操作
     *
     * @param id 编号
     * @return 媒体评论操作
     */
    MediaCommentsActionDO getMediaCommentsAction(Long id);

    /**
     * 获取单位时间内数据数量
     *
     * @param userId       用户编号
     * @param strategyType 策略类型
     * @param frequency    频次
     */
    int selectCountByStrategyAndPerUnitTime(Long userId, Integer strategyType, Integer frequency);

    /**
     * @param userId          用户编号
     * @param commentUserCode 评论人账户编号
     * @param commentsId      评论编号
     * @param strategyCode    策略编号
     * @param strategyType    策略类型
     * @param executeType     执行类型
     * @param executeContent  执行内容
     * @param executeTime     执行时间
     */
    void createMediaCommentsAction(Long userId, String commentUserCode, Long commentsId, Long strategyCode, Integer strategyType, Integer executeType, String executeContent, LocalDateTime executeTime);

    /**
     * 通过评论获取操作列表
     *
     * @param id 评论编号
     * @return 操作列表
     */
    List<MediaCommentsActionDO> getActionListByCommentId(Long id);


}