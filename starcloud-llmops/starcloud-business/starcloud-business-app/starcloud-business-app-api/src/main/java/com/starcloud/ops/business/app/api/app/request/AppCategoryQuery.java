package com.starcloud.ops.business.app.api.app.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 类别查询类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "类别查询类")
public class AppCategoryQuery implements Serializable {

    private static final long serialVersionUID = -865364644871137889L;

    /**
     * 是否统计应用数量
     */
    @Schema(description = "是否统计应用数量")
    private Boolean isAppCount;

    /**
     * 当统开启统计数量的时候，审核状态，如果不传入，则默认统计 已审核的应用数量
     */
    private List<String> audits;

}
