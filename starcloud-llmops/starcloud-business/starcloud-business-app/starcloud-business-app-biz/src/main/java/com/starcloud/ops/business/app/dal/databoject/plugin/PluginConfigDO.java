package com.starcloud.ops.business.app.dal.databoject.plugin;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("llm_material_plugin_config")
public class PluginConfigDO extends DeptBaseDO {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid
     */
    private String uid;

    /**
     * 素材库uid
     */
    private String libraryUid;

    /**
     * 插件uid
     */
    private String pluginUid;

    /**
     * 字段映射
     */
    private String fieldMap;

    /**
     * 执行参数
     */
    private String executeParams;

    /**
     * {@link com.starcloud.ops.business.app.enums.plugin.PluginBindTypeEnum}
     */
    private Integer type;

    private String bindName;

}
