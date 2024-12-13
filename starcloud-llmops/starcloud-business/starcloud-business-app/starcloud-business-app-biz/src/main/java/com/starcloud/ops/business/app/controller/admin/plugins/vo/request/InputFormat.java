package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "入参定义")
public class InputFormat {

    /**
     * 字段描述
     */
    private String variableDesc;

    /**
     * 字段名称
     */
    private String variableKey;

    /**
     * 字段类型
     */
    private String variableType;

}
