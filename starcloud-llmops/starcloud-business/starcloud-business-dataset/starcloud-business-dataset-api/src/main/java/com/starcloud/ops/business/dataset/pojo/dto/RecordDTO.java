package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class RecordDTO {

    /**
     * 相似度
     */
    private Double score;

    /**
     * 数据集id
     */
    private String datasetId;

    /**
     * 文档Id
     */
    private String documentId;

    /**
     * 分段id
     */
    private String segmentId;

    /**
     * 分段序号
     */
    private Integer position;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * tokens
     */
    private Long tokens;

    /**
     * 内容hash
     */
    private String segmentHash;

    /**
     * 状态
     */
    private String status;

    /**
     * 内容
     */
    private String content;

    /**
     * 租户id
     */
    private Long tenantId;

    private LocalDateTime createTime;

    private String creator;


}
