package com.starcloud.ops.business.dataset.pojo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "分段详情查询")
public class SegmentPageQuery extends PageParam {

    @Schema(description = "数据集Uid")
    @NotBlank(message = "数据集Uid 不能为空")
    private String datasetUid;

    @Schema(description = "文档Uid")
    @NotBlank(message = "文档Uid 不能为空")
    private String documentUid;
}
