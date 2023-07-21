package com.starcloud.ops.business.dataset.service.dto;

import lombok.Data;

import java.util.Map;

/**
 *
 *
 */
@Data
public class sourceDataUploadRespDTO {

    /**
     * 源数据ID
     */
    private Long sourceId;


    /**
     * 存储 ID
     */
    private Long storageId;


    /**
     * 存储 地址
     */
    private String storageAddress;


    /**
     * 数据类型
     */
    private String dataType;




}
