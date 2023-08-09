package com.starcloud.ops.business.app.dal.databoject.publish;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 应用发布 DO
 * </p>
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_app_publish")
@KeySequence("llm_app_publish_seq")
public class AppPublishDO extends TenantBaseDO {

    private static final long serialVersionUID = 118866759643203097L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 应用 UID
     */
    @TableField("app_uid")
    private String appUid;

    /**
     * 应用市场 UID
     */
    @TableField("market_uid")
    private String marketUid;

    /**
     * 用户提交人 ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 应用名称
     */
    @TableField("name")
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @TableField("model")
    private String model;

    /**
     * 发布版本
     */
    @TableField("version")
    private Integer version;

    /**
     * 应用类别
     */
    @TableField("categories")
    private String categories;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 发布的应用数据，一条应用的完整数据。备份，分享链接，发布应用数据，均使用该数据。
     */
    @TableField("app_info")
    private String appInfo;

    /**
     * 应用描述
     */
    @TableField("description")
    private String description;

    /**
     * 审核状态
     */
    @TableField("audit")
    private Integer audit;
}
