package com.starcloud.ops.business.app.api.market.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-28
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "应用市场分组响应类")
public class AppMarketGroupCategoryRespVO implements Serializable {

    private static final long serialVersionUID = 7780620846043834001L;

    /**
     * 分类Code
     */
    @Schema(description = "分类Code")
    private String code;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String name;

    /**
     * 父级分类Code
     */
    @Schema(description = "父级分类Code")
    private String parentCode;

    /**
     * 应用列表
     */
    @Schema(description = "应用列表")
    private List<AppMarketRespVO> appList;


}
