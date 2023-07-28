package com.starcloud.ops.business.chat.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.service.message.MpMessageService;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.ChatConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.DatesetReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.ModelConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.OpenaiCompletionReqVo;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSourceEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.chat.service.WxMpChatService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WxMpChatServiceImpl implements WxMpChatService {

    @Resource
    private AppService appService;

    @Resource
    private ThreadWithContext threadWithContext;

    @Resource
    private MpMessageService mpMessageService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void parseUrl(String url, Long mqUserId, String prompt) {
        //todo 调用数据集
        List<String> datasetUid = new ArrayList<>();
        appService.create(buildAppReqVO(datasetUid, prompt));
    }

    @Override
    public String getRecentlyChatApp(String prompt) {
        AppRespVO recently = appService.getRecently(UserContextHolder.getUserId());
        if (recently == null) {
            appService.create(buildAppReqVO(prompt));
            recently = appService.getRecently(UserContextHolder.getUserId());
        }
        return recently.getUid();
    }

    @Override
    public void chatAndReply(ChatRequestVO chatRequestVO, Long mqUserId, String openId) {
        threadWithContext.asyncExecute(() -> {
            try {
                ChatAppEntity<ChatRequestVO, JsonData> appEntity = AppFactory.factory(chatRequestVO);
                JsonData execute = appEntity.execute(chatRequestVO);
                // 回复消息
                String msg = JSONUtil.parseObj(execute.getData()).getStr("text");
                if (StringUtils.isNotBlank(msg)) {
                    sendMsg(mqUserId, msg);
                } else {
                    sendMsg(mqUserId, "AI 异常请稍后重试");
                }
            } catch (Exception e) {
                log.error("chat error", e);
                sendMsg(mqUserId, "AI 异常请稍后重试！");
            } finally {
                log.info("dele");
                redisTemplate.delete(openId + "-ready");
            }
        });
    }

    @Override
    public void sendMsg(Long mqUserId, String msg) {
        MpMessageSendReqVO messageSendReqVO = new MpMessageSendReqVO();
        messageSendReqVO.setUserId(mqUserId);
        messageSendReqVO.setContent(msg);
        messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
        mpMessageService.sendKefuMessage(messageSendReqVO);
    }

    private AppReqVO buildAppReqVO(List<String> datasetUid, String prompt) {
        AppReqVO appReqVO = buildAppReqVO(prompt);
        if (CollectionUtils.isEmpty(datasetUid)) {
            return appReqVO;
        }
        List<DatesetReqVO> datesetReqVOList = new ArrayList<>(datasetUid.size());
        for (String uid : datasetUid) {
            DatesetReqVO datesetReqVO = new DatesetReqVO(uid, true);
            datesetReqVOList.add(datesetReqVO);
        }
        appReqVO.getChatConfig().setDatesetEntities(datesetReqVOList);
        return appReqVO;
    }

    private AppReqVO buildAppReqVO(String prompt) {
        AppReqVO appReqVO = new AppReqVO();
        String name = IdUtil.fastUUID();
        appReqVO.setName(name);
        appReqVO.setModel(AppModelEnum.CHAT.name());
        appReqVO.setType(AppTypeEnum.MYSELF.name());
        appReqVO.setSource(AppSourceEnum.WX_WP.name());
        ChatConfigReqVO chatConfigReqVO = new ChatConfigReqVO();
        ModelConfigReqVO openaiModel = ModelConfigReqVO.builder().provider("openai").completionParams(new OpenaiCompletionReqVo()).build();
        chatConfigReqVO.setModelConfig(openaiModel);
        chatConfigReqVO.setPrePrompt(prompt);
        appReqVO.setChatConfig(chatConfigReqVO);
        return appReqVO;
    }


}
