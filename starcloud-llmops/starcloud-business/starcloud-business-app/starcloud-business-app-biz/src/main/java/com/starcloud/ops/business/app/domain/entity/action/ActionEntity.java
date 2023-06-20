package com.starcloud.ops.business.app.domain.entity.action;

import lombok.Data;

/**
 * 基础 action 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
public class ActionEntity {

    /**
     * 动作名称
     */
    private String name;

    /**
     * 动作描述
     */
    private String description;

    /**
     * 动作类型
     */
    private String type;

    /**
     * 动作处理器
     */
    private String handler;

    /**
     * 动作执行结果
     */
    private ActionResponse response;

}
