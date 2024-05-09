package com.starcloud.ops.business.app.api.xhs.bath.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanBatchListReqVO", description = "创作计划批次List请求")
public class CreativePlanBatchListReqVO extends PageQuery {

    private static final long serialVersionUID = 6820734679040702156L;

    /**
     * 计划UID
     */
    @Schema(description = "创作计划UID")
    private String planUid;

}
