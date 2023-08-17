package com.starcloud.ops.business.app.service.chat;

import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;

import java.util.List;

/**
 * 聊天技能服务
 */
public interface ChatSkillService {


    /**
     * 返回支持的技能列表
     */
    List<ChatSkillVO> listSkill();

    /**
     * 返回当前机器人配置的技能列表
     */
    List<ChatSkillVO> chatSkill(String appUid);


}
