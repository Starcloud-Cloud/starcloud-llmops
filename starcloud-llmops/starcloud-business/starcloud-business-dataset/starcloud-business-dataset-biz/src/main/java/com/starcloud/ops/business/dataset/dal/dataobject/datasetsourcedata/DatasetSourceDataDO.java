package com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 数据集源数据 DO
 *
 * @author 芋道源码
 */
@TableName("llm_dataset_source_data")
@KeySequence("llm_dataset_source_data_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetSourceDataDO extends TenantBaseDO {

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
     * 存储ID
     */
    private String storageId;
    /**
     * 位置
     */
    private Integer position;
    /**
     * 数据源类型（0-本地上传，1-接口上传）
     */
    private Integer dataSourceType;
    /**
     * 数据源信息
     */
    private String dataSourceInfo;
    /**
     * 数据集处理规则ID
     */
    private String datasetProcessRuleId;
    /**
     * 批次
     */
    private String batch;
    /**
     * 创建来源
     */
    private String createdFrom;
    /**
     * 字数
     */
    private Long wordCount;
    /**
     * 令牌数
     */
    private Long tokens;
    /**
     * 数据集ID
     */
    private String datasetId;
    /**
     * 创建API请求ID
     */
    private String createdApiRequestId;
    /**
     * 解析完成时间
     */
    private LocalDateTime parsingCompletedTime;
    /**
     * 清洗完成时间
     */
    private LocalDateTime cleaningCompletedTime;
    /**
     * 拆分完成时间
     */
    private LocalDateTime splittingCompletedTime;
    /**
     * 索引创建时间
     */
    private Double indexingTime;
    /**
     * 处理开始时间
     */
    private LocalDateTime processingStartedTime;
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 停止时间
     */
    private LocalDateTime stoppedTime;
    /**
     * 暂停人
     */
    private String pausedBy;
    /**
     * 暂停时间
     */
    private LocalDateTime pausedTime;
    /**
     * 禁用人
     */
    private String disabledAt;
    /**
     * 禁用时间
     */
    private LocalDateTime disabledTime;
    /**
     * 索引状态
     */
    private String indexingStatus;
    /**
     * 是否启用
     */
    private Boolean enabled;
    /**
     * 文档类型
     */
    private String docType;
    /**
     * 文档元数据
     */
    private String docMetadata;
    /**
     * 是否归档
     */
    private Boolean archived;
    /**
     * 归档人
     */
    private String archivedBy;
    /**
     * 归档原因
     */
    private String archivedReason;
    /**
     * 是否暂停
     */
    private Boolean isPaused;
    /**
     * 归档时间
     */
    private LocalDateTime archivedTime;

}