package com.starcloud.ops.business.dataset.core.handler.dto;

import lombok.Data;

/**
 * 数据上传结果 DTO
 */
@Data
public class UploadFileRespDTO {

    /**
     * 数据名称
     */
    private String name;

    /**
     * 上传返回的地址
     */
    private String filepath;

    /**
     * 数据大小
     */
    private Long size;

    /**
     * 字符数
     */
    private Long characterCount;

    /**
     * 数据类型
     */
    private String mimeType;

    /**
     * 数据扩展名
     */
    private String extension;

    /**
     * 上传状态
     */
    private Boolean status = false;



}
