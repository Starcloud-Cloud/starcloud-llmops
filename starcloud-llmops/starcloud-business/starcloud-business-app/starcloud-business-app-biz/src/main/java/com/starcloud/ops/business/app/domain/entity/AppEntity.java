package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppEntity {

    /**
     * App 唯一标识
     */
    private String uid;

    /**
     * App 名称
     */
    private String name;

    /**
     * App 版本
     */
    private Integer version;


    public String getUniqueName() {

        return this.name + this.version + this.uid;
    }


    public void executeStep() {
        // 1. 获取模版
        // 2. 获取步骤
        // 3. 执行步骤
        // 4. 保存执行结果
    }
}
