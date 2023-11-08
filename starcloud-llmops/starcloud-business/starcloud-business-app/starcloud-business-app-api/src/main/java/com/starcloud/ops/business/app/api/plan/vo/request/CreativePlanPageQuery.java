package com.starcloud.ops.business.app.api.plan.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
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
@Schema(name = "CreativePlanPageQuery", description = "创作计划分页请求")
public class CreativePlanPageQuery extends PageQuery {

    private static final long serialVersionUID = 2680070754414480259L;

    /**
     * 创作计划名称
     */
    @Schema(description = "创作计划名称")
    private String name;

    /**
     * 创作计划类型
     */
    @Schema(description = "创作计划类型")
    private String type;

    /**
     * 创作计划状态
     */
    @Schema(description = "创作计划状态")
    private String status;


}
