package com.starcloud.ops.business.dataset.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class RecordDTO {

    private Double score;

    private String id;

    private String datasetId;

    private String documentId;

    private Integer position;

    private Integer wordCount;

    private Long tokens;

    private String segmentHash;

    private Integer hitCount;

    private String status;

    private Date indexingTime;

    private String content;

    private String error;

    private Boolean disabled;

    private Long tenantId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creator;

    private String updater;

}
