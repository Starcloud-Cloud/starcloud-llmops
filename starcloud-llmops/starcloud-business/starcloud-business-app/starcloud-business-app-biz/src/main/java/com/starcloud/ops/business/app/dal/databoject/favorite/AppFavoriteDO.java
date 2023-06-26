package com.starcloud.ops.business.app.dal.databoject.favorite;

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
@TableName("llm_app_favorite")
@KeySequence("llm_app_favorite_seq")
public class AppFavoriteDO extends TenantBaseDO {

    private static final long serialVersionUID = -7652231092241634617L;

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
    @TableField("app_uid")
    private String appUid;

}
