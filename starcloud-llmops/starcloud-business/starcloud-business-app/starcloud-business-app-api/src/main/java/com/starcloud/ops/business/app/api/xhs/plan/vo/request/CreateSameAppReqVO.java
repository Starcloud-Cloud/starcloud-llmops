package com.starcloud.ops.business.app.api.xhs.plan.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021/06/22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作计划请求")
public class CreateSameAppReqVO implements java.io.Serializable {

    private static final long serialVersionUID = -1466157560511882287L;

    /**
     * 计划UID
     */
    @Schema(description = "计划UID")
    private String planUid;

    /**
     * 应用市场UID
     */
    @Schema(description = "应用市场UID")
    private String appMarketUid;

    /**
     * 是否使用应用市场配置
     */
    @Schema(description = "是否使用应用市场配置")
    private Boolean useAppMarket;
}
