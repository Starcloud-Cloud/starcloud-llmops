package com.starcloud.ops.business.app.domain.entity.params;

import com.networknt.schema.JsonSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDataDefSchema;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * jsonSchemas 结构的参数，后面做自动解析逻辑处理
 */
@Slf4j
@Data
@NoArgsConstructor
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
     * 创建一个 JsonData 对象
     *
     * @param data 数据
     * @return JsonData 对象
     */
    public static JsonData of(Object data) {
        JsonData jsonData = new JsonData();

        //默认套一层 jsonSchema
        JsonDataDefSchema jsonDataDefSchema = new JsonDataDefSchema();
        jsonDataDefSchema.setData(String.valueOf(data));

        jsonData.setData(jsonDataDefSchema);
        jsonData.setJsonSchema(JsonSchemaUtils.generateJsonSchemaStr(JsonDataDefSchema.class));

        return jsonData;
    }

    /**
     * 创建一个 JsonData 对象
     *
     * @param data 数据
     * @return JsonData 对象
     */
    public static <T> JsonData of(Object data, Class<T> tClass) {
        JsonData jsonData = new JsonData();
        jsonData.setData(data);
        jsonData.setJsonSchema(JsonSchemaUtils.generateJsonSchemaStr(tClass));

        return jsonData;
    }

    /**
     * 创建一个 JsonData 对象
     *
     * @param data 数据
     * @return JsonData 对象
     */
    public static <T> JsonData of(Object data, String jsonSchema) {
        JsonData jsonData = new JsonData();
        jsonData.setData(data);
        jsonData.setJsonSchema(jsonSchema);

        return jsonData;
    }
}
