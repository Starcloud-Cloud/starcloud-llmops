package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 应用市场分组查询类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-28
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用市场分组查询类")
public class AppMarketListGroupByCategoryQuery implements Serializable {

    private static final long serialVersionUID = -862237836706122710L;

    /**
     * 是否热门应用
     */
    @Schema(description = "是否查询热门应用")
    private Boolean isHot;


}
