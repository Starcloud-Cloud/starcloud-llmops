package com.starcloud.ops.business.app.dal.databoject.favorite;

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
     * 收藏UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 收藏类型
     */
    @TableField("type")
    private String type;

    /**
     * 应用 UID
     */
    @TableField("market_uid")
    private String marketUid;

    @TableField("style_uid")
    private String styleUid;

}
