package com.starcloud.ops.business.user.api.level.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelConfig {
    /**
     * 可使用的应用数
     */
    private Integer usableApp;

    /**
     * 可使用的基础版机器人数
     */
    private Integer usableBasicBot;

    /**
     * 可使用的微信机器人数
     */
    private Integer usableWechatBot;

    /**
     * 可使用的机器人文档数
     */
    private Integer usableBotDocument;

    /**
     * 技能插件数
     */
    private Integer usableSkillPlugin;

    /**
     * 可创建的团队数
     */
    private Integer usableTeams;


    /**
     * 团队可以添加的人数
     */
    private Integer usableTeamUsers;

}