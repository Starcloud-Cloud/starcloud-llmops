package com.starcloud.ops.business.app.api.xhs.bath.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanBatchPageReqVO", description = "创作计划批次分页请求")
public class CreativePlanBatchPageReqVO extends PageQuery {

    private static final long serialVersionUID = 315120845556696003L;

    /**
     * 计划UID
     */
    @NotBlank(message = "创作计划uid不能为空")
    @Schema(description = "创作计划UID")
    private String planUid;

}
