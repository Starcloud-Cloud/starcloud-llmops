package com.starcloud.ops.business.dataset.service.dto;

import lombok.Data;

/**
 *
 */
@Data
public class SourceDataUploadDTO {

    /**
     * 源数据 主键ID
     */
    private Long sourceDataId;

    /**
     * 源数据 UID
     */
    private String sourceDataUid;

    /**
     * 应用 ID
     */
    private String appId;
    /**
     * 批次
     */
    private String batch;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 错误信息
     */
    private String errMsg;

}
