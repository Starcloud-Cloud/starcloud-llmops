package com.starcloud.ops.business.dataset.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
@Schema(description = "文件分段命中测试")
public abstract class BaseQueryRequest {

    @Schema(description = "匹配文本")
    @NotBlank(message = "文本内容不能为空")
    private String text;

    @Schema(description = "搜索文档数")
    private Long k ;

    @Schema(description = "最低打分")
    private Double minScore;
}
