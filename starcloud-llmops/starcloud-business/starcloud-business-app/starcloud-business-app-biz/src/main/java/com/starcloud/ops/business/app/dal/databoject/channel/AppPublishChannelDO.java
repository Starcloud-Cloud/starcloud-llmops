package com.starcloud.ops.business.app.dal.databoject.channel;

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
 *
 * </p>
 *
 * @author admin
 * @since 2023-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_app_publish_channel")
@KeySequence("llm_app_publish_channel_seq")
public class AppPublishChannelDO extends TenantBaseDO {

    private static final long serialVersionUID = -983813907876642674L;

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
     * 发布媒介类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 媒介信息配置
     */
    @TableField("config")
    private String config;

    /**
     * 媒介描述
     */
    @TableField("description")
    private String description;

    /**
     * 媒介状态： 0-启用，1-禁用
     */
    @TableField("status")
    private Integer status;

}
