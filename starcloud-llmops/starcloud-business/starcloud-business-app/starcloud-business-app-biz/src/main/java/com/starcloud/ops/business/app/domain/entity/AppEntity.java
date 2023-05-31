package com.starcloud.ops.business.app.domain.entity;

import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class AppEntity {


    private String uid;

    private String name;

    private Integer version;

    private String content;


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
