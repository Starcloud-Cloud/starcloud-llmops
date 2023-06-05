package com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage;

import lombok.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 数据集源数据存储 DO
 *
 * @author 芋道源码
 */
@TableName("llm_dataset_sourcedata_storage")
@KeySequence("llm_dataset_sourcedata_storage_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetStorageDO extends BaseDO {

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
    private String key;
    /**
     * 存储类型
     */
    private String storageType;
    /**
     * 大小
     */
    private Integer size;
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