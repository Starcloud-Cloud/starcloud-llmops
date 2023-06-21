package com.starcloud.ops.business.app.api.app.vo.response;

import lombok.Data;


/**
 * 应用执行返回实体
 */
@Data
public class ExecuteAppRespVO {


    private String conversationId;

    private String messageId;

    private String stepId;

    private String scene;


    private String user;

    private String endUser;
}
