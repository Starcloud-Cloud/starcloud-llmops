package com.starcloud.ops.business.app.domain.entity.params;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * jsonSchemas 结构的参数，后面做自动解析逻辑处理
 */
@Slf4j
@Data
public class JsonData extends BaseDataEntity {

    private Object data;

    private Object jsonSchemas;

    public <R> R parse(Class<R> input) {
        return JSONUtil.toBean(this.getData().toString(), input);
    }

    public String toJson() {
        return JSONUtil.toJsonStr(this.getData());
    }

    public static JsonData of(Object data) {

        JsonData jsonData = new JsonData();

        jsonData.setData(data);
        return jsonData;
    }

}
