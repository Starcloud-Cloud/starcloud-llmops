package com.starcloud.ops.business.app.domain.entity2.action;

import lombok.Data;

import java.util.List;

/**
 * 聊天应用配置DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
public class ActionEntity {


    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤描述
     */
    private String description;



    /**
     * 步骤执行结果
     */
    private ActionResponse actionResponse;

}
