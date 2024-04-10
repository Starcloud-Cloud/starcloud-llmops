package com.starcloud.ops.business.app.dal.databoject.comment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 媒体评论 DO
 *
 * @author starcloudadmin
 */
@TableName("marketing_media_comments")
@KeySequence("marketing_media_comments_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaCommentsDO extends BaseDO {

    /**
     * 评论编号
     */
    @TableId
    private Long id;
    /**
     * 账号类型（10-小红书，20-抖音）
     */
    private Integer accountType;
    /**
     * 账号 ID
     */
    private String accountCode;
    /**
     * 账号名称
     */
    private String accountName;
    /**
     * 账号头像
     */
    private String accountAvatar;
    /**
     * 媒体编号
     */
    private String mediaCode;
    /**
     * 媒体标题
     */
    private String mediaTitle;
    /**
     * 媒体封面
     */
    private String mediaCover;
    /**
     * 评论人用户编号
     */
    private String commentUserCode;
    /**
     * 评论人用户昵称
     */
    private String commentUserName;
    /**
     * 评论人用户头像
     */
    private String commentUserAvatar;
    /**
     * 评论内容
     */
    private String commentContent;
    /**
     * 评论编号
     */
    private String commentCode;

    /**
     * 回复状态
     */
    private Integer responseStatus;
    /**
     * 点赞状态
     */
    private Integer likeStatus;
    /**
     * 关注内容
     */
    private Integer concernStatus;


    /**
     * 点赞命中策略编号
     */
    private Long likeStrategyId;
    /**
     * 回复命中策略编号
     */
    private Long responseStrategyId;
    /**
     * 关注命中策略编号
     */
    private Long concernStrategyId;

}