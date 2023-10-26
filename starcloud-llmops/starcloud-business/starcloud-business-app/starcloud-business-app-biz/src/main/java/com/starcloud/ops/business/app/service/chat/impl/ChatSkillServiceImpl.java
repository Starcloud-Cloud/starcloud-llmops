package com.starcloud.ops.business.app.service.chat.impl;

import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.AllChatSkilVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.WebSearchConfigEntity;
import com.starcloud.ops.business.app.domain.entity.skill.AppWorkflowSkill;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.datasearch.ImageSearchHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.NewsSearchHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.TiYuBaiduHandler;
import com.starcloud.ops.business.app.domain.handler.datasearch.WebSearch2DocHandler;
import com.starcloud.ops.business.app.domain.handler.imggeneration.ImageGenerationHandler;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.chat.ChatSkillService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppService appService;

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

    @Override
    public AllChatSkilVO allChatSkill() {
        List<ChatSkillVO> systemSkill = this.listSystemSkill();
        AppMarketPageQuery marketPageQuery = new AppMarketPageQuery();
        marketPageQuery.setPageNo(1);
        marketPageQuery.setPageSize(1000);
        List<AppMarketRespVO> marketApps = appMarketService.page(marketPageQuery).getList()
                .stream().filter(appMarketRespVO -> {
                    return appMarketRespVO.getTags() != null
                            && appMarketRespVO.getTags().contains("ChatSkill")
                            && appMarketRespVO.getStepCount() <= 1;
                })
                .collect(Collectors.toList());

        AppPageQuery appPageQuery = new AppPageQuery();
        appPageQuery.setPageNo(1);
        appPageQuery.setPageSize(1000);
        List<AppRespVO> apps = appService.page(appPageQuery).getList();
        AllChatSkilVO allChatSkilVO = new AllChatSkilVO();
        allChatSkilVO.setSystemSkill(systemSkill);
        allChatSkilVO.setAppRespList(apps);
        allChatSkilVO.setMarketRespList(marketApps);
        return allChatSkilVO;
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
            add(ChatSkillVO.buildFromHandler(new WebSearch2DocHandler()));
            add(ChatSkillVO.buildFromHandler(new ImageGenerationHandler()));

            add(ChatSkillVO.buildFromHandler(new ImageSearchHandler()));

            add(ChatSkillVO.buildFromHandler(new NewsSearchHandler()));
            add(ChatSkillVO.buildFromHandler(new TiYuBaiduHandler()));
        }};

        return skillVOS;
    }

}
