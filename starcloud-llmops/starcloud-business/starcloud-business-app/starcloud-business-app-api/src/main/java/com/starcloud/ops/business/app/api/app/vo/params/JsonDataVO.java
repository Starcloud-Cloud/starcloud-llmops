package com.starcloud.ops.business.app.api.app.vo.params;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * jsonSchemas 结构的参数，后面做自动解析逻辑处理
 */
@Slf4j
@Data
@NoArgsConstructor
public class JsonDataVO extends BaseDataVO {

    private static final long serialVersionUID = -4517763592727025312L;

    /**
     * 返回的数据
     */
    private Object data;

    /**
     * jsonSchema 数据
     */
    private String jsonSchema;

}
