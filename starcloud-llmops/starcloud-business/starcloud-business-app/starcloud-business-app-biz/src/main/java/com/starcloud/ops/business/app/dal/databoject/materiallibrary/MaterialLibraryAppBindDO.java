package com.starcloud.ops.business.app.dal.databoject.materiallibrary;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import lombok.*;

/**
 * 应用素材绑定 DO
 *
 * @author starcloudadmin
 */
@TableName("llm_material_library_app_bind")
@KeySequence("llm_material_library_app_bind_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialLibraryAppBindDO extends DeptBaseDO {

    /**
     * 状态
     */
    private static int ENABLE_STATUS_NUM = 1;

    /**
     * 主键(自增策略)
     */
    @TableId
    private Long id;
    /**
     * 素材编号
     */
    private Long libraryId;
    /**
     * 应用类型
     */
    private Integer appType;
    /**
     * 应用编号
     */
    private String appUid;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 状态
     */
    private Boolean status;

}