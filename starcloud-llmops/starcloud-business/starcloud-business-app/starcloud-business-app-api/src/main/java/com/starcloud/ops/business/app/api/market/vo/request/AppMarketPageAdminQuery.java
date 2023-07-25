package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 应用查询类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用查询类")
public class AppMarketPageAdminQuery extends AppMarketPageQuery {

    private static final long serialVersionUID = -5733167670113264441L;

    @Schema(description = "应用 uid")
    private String uid;

    /**
     * 应用审核状态
     */
    @Schema(description = "应用审核状态")
    private List<Integer> audits;

}
