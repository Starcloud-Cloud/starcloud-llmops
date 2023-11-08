package com.starcloud.ops.business.app.controller.admin.xhs.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class XhsCreativeQueryReq {

    private String planUid;

    /**
     * 执行任务类型 picture/copy_writing
     */
    @NotBlank
    private String type;

    /**
     * 一次查询的数据量
     */
    private Integer bathCount;

    /**
     * 是否只执行重试任务
     */
    private Boolean retryProcess;

    /**
     * 执行状态  init  execute_error
     */
    @NotBlank
    private String executeStatus;

    /**
     * 执行计划状态
     */
    @NotBlank
    private String planStatus;


}
