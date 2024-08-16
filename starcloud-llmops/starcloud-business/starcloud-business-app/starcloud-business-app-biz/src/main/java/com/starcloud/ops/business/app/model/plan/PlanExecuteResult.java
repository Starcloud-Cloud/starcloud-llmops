package com.starcloud.ops.business.app.model.plan;

import lombok.Data;

import java.io.Serializable;

/**
 * 计划执行结果
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class PlanExecuteResult implements Serializable {

    private static final long serialVersionUID = 2321480959363850926L;

    private String planUid;

    private String batchUid;

    private String warning;
}
