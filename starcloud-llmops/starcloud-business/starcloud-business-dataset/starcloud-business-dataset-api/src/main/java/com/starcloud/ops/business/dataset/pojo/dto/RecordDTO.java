package com.starcloud.ops.business.dataset.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(description = "分段详情")
public class RecordDTO {

    /**
     * 相似度
     */
    @Schema(description = "相似度")
    private Double score;

    /**
     * 数据集id
     */
    @Schema(description = "数据集id")
    private String datasetId;

    /**
     * 文档Id
     */
    @Schema(description = "文档Id")
    private String documentId;

    /**
     * 分段id
     */
    @Schema(description = "分段Id")
    private String segmentId;

    /**
     * 分段序号
     */
    @Schema(description = "分段序号")
    private Integer position;

    /**
     * 字数
     */
    @Schema(description = "字数")
    private Integer wordCount;

    /**
     * tokens
     */
    @Schema(description = "索引消耗的token")
    private Long tokens;

    /**
     * 内容hash
     */
    @Schema(description = "内容hash值")
    private String segmentHash;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

    /**
     * 内容
     */
    @Schema(description = "分段内容")
    private String content;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "索引时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String creator;


}
