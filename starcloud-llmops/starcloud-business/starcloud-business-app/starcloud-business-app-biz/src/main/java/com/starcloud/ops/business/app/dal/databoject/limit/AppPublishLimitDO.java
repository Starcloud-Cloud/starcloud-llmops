package com.starcloud.ops.business.app.dal.databoject.limit;

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
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_app_publish_limit")
@KeySequence("llm_app_publish_limit_seq")
public class AppPublishLimitDO extends TenantBaseDO {

    private static final long serialVersionUID = 8154289831234914323L;

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
     * 发布 UID
     */
    @TableField("app_uid")
    private String appUid;

    /**
     * 发布 UID
     */
    @TableField("publish_uid")
    private String publishUid;

    /**
     * 发布渠道UID
     */
    @TableField("channel_uid")
    private String channelUid;

    /**
     * 是否启用总量限制，0：禁用，1：启用
     */
    @TableField("quota_enable")
    private Boolean quotaEnable;

    /**
     * 总量限制配置
     */
    @TableField("quota_config")
    private String quotaConfig;

    /**
     * 是否启用频率限制，0：禁用，1：启用
     */
    @TableField("rate_enable")
    private Boolean rateEnable;

    /**
     * 频率限制配置
     */
    @TableField("rate_config")
    private String rateConfig;

    /**
     * 是否启用用户用量限制，0：禁用，1：启用
     */
    @TableField("user_quota_enable")
    private Boolean userQuotaEnable;

    /**
     * 用户用量限制配置
     */
    @TableField("user_quota_config")
    private String userQuotaConfig;

    /**
     * 是否启用广告位限制，0：禁用，1：启用
     */
    @TableField("advertising_enable")
    private Boolean advertisingEnable;

    /**
     * 广告位限制配置
     */
    @TableField("advertising_config")
    private String advertisingConfig;
}
