package com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 数据集源数据存储 DO
 *
 * @author 芋道源码
 */
@TableName("llm_dataset_storage")
@KeySequence("llm_dataset_storage_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetStorageDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 编号
     */
    private String uid;
    /**
     * 名称
     */
    private String name;
    /**
     * 数据类型
     */
    private String type;
    /**
     * 键
     */
    private String storageKey;
    /**
     * 存储类型
     */
    private String storageType;
    /**
     * 大小
     */
    private Long size;
    /**
     * MIME类型
     */
    private String mimeType;
    /**
     * 是否已使用
     */
    private Boolean used;
    /**
     * 使用者ID
     */
    private String usedBy;
    /**
     * 使用时间
     */
    private LocalDateTime usedAt;
    /**
     * 哈希值
     */
    private String hash;

}