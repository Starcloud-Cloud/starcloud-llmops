package com.starcloud.ops.business.app.domain.entity.params;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * jsonSchemas 结构的参数，后面做自动解析逻辑处理
 */
@Slf4j
@Data
public class JsonData extends BaseDataEntity {

    /**
     * 返回的数据
     */
    private Object data;

    /**
     * jsonSchema 数据
     */
    private String jsonSchema;

    /**
     * 校验jsonSchema
     */
    public void validateJsonSchema() {
        JsonSchemaUtils.validate(this.jsonSchema);
    }

    public static JsonData of(Object data) {

        JsonData jsonData = new JsonData();

        jsonData.setData(data);
        return jsonData;
    }

}
