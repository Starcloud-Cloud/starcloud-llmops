package com.starcloud.ops.business.dataset.core.handler.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 数据上传结果 DTO
 */
@Data
public class UploadContentDTO {

    /**
     * 批次
     */
    private String batch;

    /**
     * 数据集 ID
     */
    private Long datasetId;

    /**
     * 数据名称
     */
    private String name;

    /**
     * 数据描述
     */
    private String description;

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
     * 初始地址
     */
    private String initAddress;


    /**
     * 数据类型
     */
    private String dataType;


    /**
     * 错误信息
     */
    private String errMsg;


    /**
     * 错误code
     */
    private String errCode;

    /**
     * 分块是否同步
     */
    private Boolean splitSync;

    /***
     * 清洗是否同步
     */
    private Boolean cleanSync;

    /**
     * 索引是否同步
     */
    private Boolean indexSync;

    /**
     * 是否生成总结
     */
    private Boolean enableSummary;
}
