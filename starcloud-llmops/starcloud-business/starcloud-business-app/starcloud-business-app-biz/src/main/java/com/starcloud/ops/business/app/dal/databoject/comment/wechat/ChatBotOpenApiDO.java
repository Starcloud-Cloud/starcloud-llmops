package com.starcloud.ops.business.app.dal.databoject.comment.wechat;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信对话开放平台 开放 API 实体类
 */
@Data
@TableName("system_user_post")
@KeySequence("system_user_post_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@EqualsAndHashCode(callSuper = true)
public class ChatBotOpenApiDO extends BaseDO {

    /**
     * 自增主键
     */
    @TableId
    private Long id;
    /**
     * 所属用户 ID
     *
     * 关联 {@link AdminUserDO#getId()}
     */
    private Long userId;
    /**
     * 开放 API 的 token
     */
    private String token;
    /**
     * 开放 API 的 secret
     */
    private String appSecret;
    /**
     * 开放 API 的 webhook
     */
    private String webhook;
    /**
     * 开放 API 的 webhook 密钥
     */
    private String webhookSecret;

}
