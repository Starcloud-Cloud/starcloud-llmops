package com.starcloud.ops.business.app.dal.databoject.operate;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应用操作管理表DO，operate 表示操作类型，LIKE 标识喜欢，VIEW 标识查看，DOWNLOAD 标识下载
 *
 * @author admin
 * @since 2023-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_app_operate")
@KeySequence("llm_app_operate_seq")
public class AppOperateDO extends TenantBaseDO {

    private static final long serialVersionUID = -1645210694233110985L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作用户
     */
    @TableField("user")
    private String user;

    /**
     * 应用 UID
     */
    @TableField("template_uid")
    private String templateUid;

    /**
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 操作类型
     */
    @TableField("operate")
    private String operate;

}
