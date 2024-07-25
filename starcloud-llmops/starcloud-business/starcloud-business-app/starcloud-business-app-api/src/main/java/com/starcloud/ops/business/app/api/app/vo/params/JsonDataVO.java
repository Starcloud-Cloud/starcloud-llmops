package com.starcloud.ops.business.app.api.app.vo.params;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * jsonSchemas 结构的参数，后面做自动解析逻辑处理
 */
@Slf4j
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonDataVO extends BaseDataVO {

    private static final long serialVersionUID = -4517763592727025312L;

    /**
     * 传入的数据
     */
    @Schema(description = "传入的数据")
    private Object data;

    /**
     * jsonSchema 配置
     */
    @Schema(description = "jsonSchema配置")
    private String jsonSchema;


    public static JsonDataVO of(String jsonSchema, Object data)  {

        JsonDataVO vo = new JsonDataVO();
        vo.setData(data);
        vo.setJsonSchema(jsonSchema);
        return vo;
    }

    public static JsonDataVO of(String jsonSchema)  {

        JsonDataVO vo = new JsonDataVO();
        vo.setJsonSchema(jsonSchema);
        return vo;
    }
}
