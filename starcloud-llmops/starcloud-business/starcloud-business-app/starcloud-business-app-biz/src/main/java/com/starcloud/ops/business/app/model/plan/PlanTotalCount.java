package com.starcloud.ops.business.app.model.plan;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class PlanTotalCount implements java.io.Serializable {

    private static final long serialVersionUID = 243188967502864505L;

    private Integer total;

    private String warning;

    public static PlanTotalCount of(Integer total, String warning) {
        PlanTotalCount totalCount = new PlanTotalCount();
        totalCount.setTotal(total);
        totalCount.setWarning(warning);
        return totalCount;
    }

    public static PlanTotalCount of(Integer total) {
        PlanTotalCount totalCount = new PlanTotalCount();
        totalCount.setTotal(total);
        totalCount.setWarning("");
        return totalCount;
    }
}
