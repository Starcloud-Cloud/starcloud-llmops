package com.starcloud.ops.business.dataset.core.handler.dto;

import lombok.Data;

/**
 * 数据上传结果 DTO
 */
@Data
public class UploadResult {

    /**
     * 数据集 ID
     */
    private String datasetId;

    /**
     * 数据 编号 UID
     */
    private String sourceDataUid;

    /**
     * 数据 主键 ID
     */
    private Long sourceDataId;

    /**
     * 数据名称
     */
    private String name;

    /**
     * 状态
     */
    private Boolean status;


    /**
     * 错误信息
     */
    private String errMsg;

}
