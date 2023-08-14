package com.starcloud.ops.business.app.domain.entity.skill;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 针对不同类型的技能定制的配置信息
 */
@Slf4j
@Data
public class SkillCustomConfig {

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    private String uid;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    private String model;

    private String name;

    private String description;

    /**
     * 显示的类型
     */
    private String showType;

}
