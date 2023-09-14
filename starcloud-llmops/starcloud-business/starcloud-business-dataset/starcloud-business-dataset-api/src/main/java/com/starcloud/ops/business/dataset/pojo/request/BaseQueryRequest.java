package com.starcloud.ops.business.dataset.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Data
@Schema(description = "文件分段命中测试")
public abstract class BaseQueryRequest {

    @Schema(description = "匹配文本")
    @NotBlank(message = "文本内容不能为空")
    private String text;

    @Schema(description = "搜索文档数",defaultValue = "2")
    @Max(value = 5, message = "k 最大值为5")
    private Long k;

    @Schema(description = "最低打分")
    @Max(value = 1, message = "minScore 最大值为1")
    @Min(value = 0, message = "minScore 最小值为0")
    private Double minScore;
}
