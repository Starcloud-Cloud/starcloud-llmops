package com.starcloud.ops.business.app.domain.entity.params;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * jsonSchemas 结构的参数，后面做自动解析逻辑处理
 */
@Slf4j
@Data
public class JsonParamsEntity extends BaseParamsEntity {

    private Object data;

    private Object jsonSchemas;

}
