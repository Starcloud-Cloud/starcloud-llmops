package com.starcloud.ops.business.dataset.service.dto;

import lombok.Data;

import java.util.List;

/**
 *
 *
 */
@Data
public class SourceDataUploadDTO {

    /**
     * 数据集 ID
     */
    private String datasetId;

    /**
     * 源数据 ID
     */
    private String sourceDataId;
    /**
     * 批次
     */
    private String batch;

    /**
     * 状态
     */
    private Boolean status;


}
