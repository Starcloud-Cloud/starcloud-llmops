package com.starcloud.ops.business.app.api.xhs.plan.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanListQuery", description = "创作计划列表请求")
public class CreativePlanListQuery implements java.io.Serializable {

    private static final long serialVersionUID = 8118507177135519788L;

    /**
     * 创作计划uid
     */
    @Schema(description = "创作计划uid")
    private String uid;

    /**
     * 创作计划状态
     */
    @Schema(description = "创作计划状态")
    private String status;

}
