package com.starcloud.ops.business.app.api.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 应用执行进度
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "应用执行进度")
public class AppExecuteProgress implements java.io.Serializable {

    private static final long serialVersionUID = -2389223281767048800L;

    /**
     * 总步骤数量
     */
    @Schema(description = "总步骤数量")
    private Integer totalStepCount;

    /**
     * 执行成功步骤数量
     */
    @Schema(description = "执行成功步骤数量")
    private Integer successStepCount;

    /**
     * 当前执行步骤索引
     */
    @Schema(description = "当前执行步骤索引")
    private Integer currentStepIndex;


}
