package com.starcloud.ops.business.app.service.chat.impl;

import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkill;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.datasearch.DocSearchHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.GoogleSearchHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.NewsSearchHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.WebSearch2DocHandler;
import com.starcloud.ops.business.app.service.chat.ChatSkillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 聊天技能服务
 */
@Slf4j
@Service
public class ChatSkillServiceImpl implements ChatSkillService {

    /**
     * 返回支持的技能列表
     */
    @Override
    public List<ChatSkillVO> listSkill() {

        //系统的
        List<ChatSkillVO> systemSkill = this.listSystemSkill();


        return systemSkill;
    }


    @Override
    public List<ChatSkillVO> chatSkill(String appUid) {

        List<ChatSkillVO> chatSkillVOS = new ArrayList<>();

        //读取配置,网页爬取技能
        ChatAppEntity chatAppEntity = AppFactory.factoryChatApp(appUid);
        WebSearchConfigEntity webSearchConfig = chatAppEntity.getChatConfig().getWebSearchConfig();
        if (webSearchConfig != null && webSearchConfig.getEnabled()) {
            chatSkillVOS.add(ChatSkillVO.buildFromHandler(new WebSearch2DocHandler()));
        }

        //读取配置,AI应用技能
        List<AppWorkflowSkill> workflowSkills = chatAppEntity.getChatConfig().getAppWorkflowSkills();
        chatSkillVOS.addAll(Optional.ofNullable(workflowSkills).orElse(new ArrayList<>()).stream().map(appWorkflowSkill -> {
            return ChatSkillVO.buildFromHandler(appWorkflowSkill);
        }).collect(Collectors.toList()));

        //API技能

        return chatSkillVOS;
    }


    /**
     * 系统自带的 技能
     * <p>
     * 互联网搜索
     * 互联网新闻
     *
     * @return
     */
    protected List<ChatSkillVO> listSystemSkill() {

        List<ChatSkillVO> skillVOS = new ArrayList<ChatSkillVO>() {{
            add(ChatSkillVO.buildFromHandler(new DocSearchHandler()));
            add(ChatSkillVO.buildFromHandler(new GoogleSearchHandler()));
            add(ChatSkillVO.buildFromHandler(new NewsSearchHandler()));
            add(ChatSkillVO.buildFromHandler(new WebSearch2DocHandler()));
        }};

        return skillVOS;
    }

}
