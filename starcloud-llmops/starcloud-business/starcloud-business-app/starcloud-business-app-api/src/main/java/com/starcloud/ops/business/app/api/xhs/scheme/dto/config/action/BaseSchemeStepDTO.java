package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;

public abstract class BaseSchemeStepDTO {


    public String name;

    /**
     * 唯一
     */
    private String code;


    /**
     * 转换到应用参数
     */
    public abstract void convertApp(WorkflowStepWrapperRespVO workflowStepWrapperRespVO);

    /**
     * 转换到创作方案参数
     */
    public abstract void convertCreative();

}
