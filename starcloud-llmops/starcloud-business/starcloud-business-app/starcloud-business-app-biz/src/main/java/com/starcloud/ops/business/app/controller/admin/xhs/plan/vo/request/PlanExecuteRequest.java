package com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class PlanExecuteRequest implements Serializable {

    private static final long serialVersionUID = 3007339335473881642L;

    /**
     * 计划UID
     */
    @Schema(description = "计划UID")
    private String uid;

    /**
     * 计划执行参数
     */
    @Schema(description = "计划执行参数")
    private Map<String, Object> params;

}
