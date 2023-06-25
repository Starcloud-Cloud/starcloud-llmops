package com.starcloud.ops.business.app.api.app.vo.request;


import lombok.Data;

/**
 * 应用执行请求实体
 */
@Data
public class ExecuteAppReqVO {

    /**
     * 应用uid
     */
    String appId;

    /**
     * 执行场景
     */
    String scene;


    /**
     * 应用配置
     */
    AppReqVO appRequest;


    /**
     * 执行应用的步骤
     */
    String stepId;
}
