package com.starcloud.ops.business.app.domain.entity.params;

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
        jsonData.setData(data);
        return jsonData;
    }

}
