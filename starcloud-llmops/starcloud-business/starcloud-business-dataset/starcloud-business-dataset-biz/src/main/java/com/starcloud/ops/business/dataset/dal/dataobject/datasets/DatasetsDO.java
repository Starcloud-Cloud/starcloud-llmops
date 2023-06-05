package com.starcloud.ops.business.dataset.dal.dataobject.datasets;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 数据集 DO
 *
 * @author 芋道源码
 */
@TableName("llm_datasets")
@KeySequence("llm_datasets_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetsDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 数据集编号
     */
    private String uid;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 提供商
     */
    private String provider;
    /**
     * 权限 (0-私有，1-租户共享，2-全体共享)
     */
    private Integer permission;
    /**
     * 数据源类型
     */
    private String sourceType;
    /**
     * 索引技术
     */
    private String indexingModel;
    /**
     * 索引结构
     */
    private String indexStruct;
    /**
     * 是否启用
     */
    private Boolean enabled;

}