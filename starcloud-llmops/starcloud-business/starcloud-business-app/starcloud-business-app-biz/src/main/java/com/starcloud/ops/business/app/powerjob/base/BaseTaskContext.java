package com.starcloud.ops.business.app.powerjob.base;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import tech.powerjob.worker.core.processor.TaskContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础任务 上下文
 */
@Slf4j
public abstract class BaseTaskContext {

    protected abstract Object getLog();

    public abstract String getParams();

    public abstract void setParams(String str);

    protected abstract Long getInstanceId();

    protected abstract String getInstanceParams();

    /**
     * 临时参数
     */
    protected Map<String, Object> objectMap = new HashMap<>();

    public <O> O getObjectMap(String key) {
        return (O) objectMap.get(key);
    }

    public <O> void putObjectMap(String key, O val) {
        objectMap.put(key, val);
    }

    public <T> T getParams(Class<T> tClass) {

        try {
            return JSON.parseObject(getParams(), tClass);
        } catch (Exception e) {
            log.warn("BaseTaskContext#getParams is fail: {}. {} to {}", e.getMessage(), getParams(), tClass.getSimpleName());
        }

        return null;
    }

    public TaskContext getTaskContext() {
        PowerJobTaskContext powerJobTaskContext = (PowerJobTaskContext) this;
        return powerJobTaskContext.getTaskContext();
    }

}
