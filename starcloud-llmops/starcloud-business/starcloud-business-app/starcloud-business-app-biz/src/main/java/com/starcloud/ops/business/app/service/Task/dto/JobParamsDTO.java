package com.starcloud.ops.business.app.service.Task.dto;

import lombok.Data;

@Data
public class JobParamsDTO {

    /**
     * 计划uid
     */
    private String planUid;

    /**
     * 强制执行  忽略重试次数
     */
    private Boolean force;
}
