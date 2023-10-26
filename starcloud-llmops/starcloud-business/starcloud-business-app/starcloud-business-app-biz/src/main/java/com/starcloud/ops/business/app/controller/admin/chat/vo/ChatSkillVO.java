package com.starcloud.ops.business.app.controller.admin.chat.vo;


import com.starcloud.ops.business.app.domain.entity.skill.BaseSkillEntity;
import com.starcloud.ops.business.app.domain.handler.common.BaseToolHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "技能")
@Data
public class ChatSkillVO {

    @Schema(description = "技能类型")
    private String type;

    @Schema(description = "技能code")
    private String code;

    @Schema(description = "技能名称")
    private String name;

    @Schema(description = "技能描述")
    private String desc;

    @Schema(description = "技能图标")
    private String icon;

    @Schema(description = "使用说明")
    private String usage;

    @Schema(description = "技能标签")
    private List<String> tags;


    /**
     * 根据 handler 去创建聊天时的技能VO
     *
     * @param handler
     * @return
     */
    public static ChatSkillVO buildFromHandler(BaseToolHandler handler) {

        ChatSkillVO skillVO = new ChatSkillVO();

        skillVO.setType("system");
        skillVO.setCode(handler.getName());
        skillVO.setName(handler.getUserName());
        skillVO.setDesc(handler.getUserDescription());
        skillVO.setTags(handler.getTags());
        skillVO.setIcon(handler.getIcon());
        skillVO.setUsage(handler.getUsage());

        return skillVO;
    }


    /**
     * 根据 skill 去创建聊天时的技能VO，应用，GPT，API 之类的
     *
     * @param baseSkillEntity
     * @return
     */
    public static ChatSkillVO buildFromHandler(BaseSkillEntity baseSkillEntity) {

        ChatSkillVO skillVO = new ChatSkillVO();

        skillVO.setType(baseSkillEntity.getType().name());
        skillVO.setCode(baseSkillEntity.getName());
        skillVO.setName(baseSkillEntity.getUserName());
        skillVO.setDesc(baseSkillEntity.getUserDesc());
        skillVO.setTags(baseSkillEntity.getTags());
        skillVO.setIcon(baseSkillEntity.getIcon());

        return skillVO;
    }


}
