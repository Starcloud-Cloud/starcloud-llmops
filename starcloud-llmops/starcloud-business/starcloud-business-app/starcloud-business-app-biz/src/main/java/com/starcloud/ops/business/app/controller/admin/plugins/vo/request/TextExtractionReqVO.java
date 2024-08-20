package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@Schema(description = "文本智能提取插件请求")
public class TextExtractionReqVO {

    @Schema(description = "字段提取说明")
    @NotEmpty(message = "")
    private Map<String, String> define;

    @Schema(description = "文本内容")
    @NotBlank(message = "")
    private String parseText;
}
