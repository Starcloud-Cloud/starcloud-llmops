package com.starcloud.ops.business.dataset.core.handler.dto;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;

/**
 * 数据上传结果 DTO
 */
@Data
public class UploadResultDTO {

    /**
     * 是否同步
     */
    private Boolean sync;
    /**
     * 批次
     */
    private String batch;
    /**
     * 分割规则
     */
    private SplitRule splitRule;

    /**
     * 数据集 ID
     */
    private String datasetId;

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


    /**
     * 数据模型
     */
    private Integer dataModel;


    /**
     * 数据类型
     */
    private String dataType;

}
