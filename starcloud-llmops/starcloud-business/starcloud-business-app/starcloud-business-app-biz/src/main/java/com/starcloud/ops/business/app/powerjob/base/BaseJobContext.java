package com.starcloud.ops.business.app.powerjob.base;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础任务 上下文
 */
@Slf4j
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseJobContext {

    private Object params;

    public <T> T getParams(Class<T> tClass) {
        return JSON.parseObject(JSON.toJSONString(params), tClass);
    }


}
