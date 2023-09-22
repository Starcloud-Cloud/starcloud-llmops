package com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
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
     * 描述
     */
    private String description;

    /**
     * 数据集ID
     */
    private Long datasetId;
    /**
     * 存储ID
     */
    private Long storageId;
    /**
     * 位置
     */
    private Long position;

    /**
     * 数据模型（0-问答，1-文档）
     */
    private Integer dataModel;
    /**
     * 数据源类型 文档 HTML 字符串
     */
    private String dataType;
    /**
     * 数据集处理规则ID
     */
    private Long ruleId;
    /**
     * 批次
     */
    private String batch;

    /**
     * 清洗内容存储 ID
     */
    private Long cleanStorageId;

    /**
     * 总结
     */
    private String summary;

    /**
     * 总结状态
     */
    private Integer summaryStatus;

    /**
     * 总结模型
     */
    private String summaryModel;

    /**
     * 总结消耗的 Token
     */
    private Long summaryTokens;

    /**
     * 总结总价
     */
    private BigDecimal summaryTotalPrice;

    /**
     * embedding 的 Token 消耗
     */
    private String embeddingTokens;
    /**
     * embedding 单价
     */
    private BigDecimal embeddingUnitPrice;

    /**
     * embedding总价
     */
    private BigDecimal embeddingTotalPrice;


    /**
     * 总结
     */
    private String dataSourceInfo;

    /**
     * 同步配置
     */
    private String syncConfig;
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
     * 错误代码
     */
    private String errorCode;
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
    /**
     * 状态
     */
    private Integer status;
    /**
     * 游客 ID
     */
    private Long endUser;


}