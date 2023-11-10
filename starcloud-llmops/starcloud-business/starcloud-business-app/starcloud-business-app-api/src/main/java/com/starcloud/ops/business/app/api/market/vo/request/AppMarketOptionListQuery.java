package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-21
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppMarketOptionListQuery", description = "应用基础请求实体")
public class AppMarketOptionListQuery extends AppMarketListQuery {

    private static final long serialVersionUID = 1781465931721116577L;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private String tagType;

}
