package com.starcloud.ops.business.user.service.notify.impl;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyTemplateDO;
import cn.iocoder.yudao.module.system.service.notify.NotifyMessageService;
import cn.iocoder.yudao.module.system.service.notify.NotifySendService;
import cn.iocoder.yudao.module.system.service.notify.NotifyTemplateService;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyReqDTO;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyResultDTO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.CreateNotifyReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.business.user.enums.notify.NotifyMediaEnum;
import com.starcloud.ops.business.user.enums.notify.NotifyTemplateEnum;
import com.starcloud.ops.business.user.service.notify.NotifyService;
import com.starcloud.ops.business.user.service.notify.NotifyFactory;
import com.starcloud.ops.business.user.service.notify.adapter.NotifyMediaAdapter;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotifyServiceImpl implements NotifyService {

    @Resource
    private NotifyMessageService notifyMessageService;

    @Resource
    private NotifyTemplateService templateService;

    @Resource
    private NotifyFactory notifyMediaFactory;

    @Resource
    private NotifySendService notifySendService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public Map<String, List<Option>> metaData() {
        Map<String, List<Option>> result = new HashMap<>();
        result.put("mediaTypes", NotifyMediaEnum.options());
        result.put("template", NotifyTemplateEnum.options());
        return result;
    }

    @Override
    public void sendNotify(Long logId) {
        String key = "send_notify_" + logId;
        RLock lock = redissonClient.getLock(key);
        if (!lock.tryLock()) {
            log.warn("locked logId = {}", logId);
            return;
        }
        try {
            NotifyMessageDO notifyMessage = notifyMessageService.getNotifyMessage(logId);
            NotifyTemplateDO template = templateService.getNotifyTemplateByCodeFromCache(notifyMessage.getTemplateCode());
            if (Boolean.TRUE.equals(notifyMessage.getSent())) {
                log.warn("已发送 logId = {}", logId);
                return;
            }
            if (!CollectionUtils.isEmpty(notifyMessage.getMediaTypes())) {
                for (Integer mediaType : notifyMessage.getMediaTypes()) {
                    doSend(logId, notifyMessage.getUserId(), mediaType,
                            notifyMessage.getTemplateContent(), notifyMessage.getTemplateParams(), template.getRemoteTemplateCode());
                }
            }
            notifyMessageService.updateById(new NotifyMessageDO().setId(logId).setSent(true));
        } catch (Exception e) {
            log.error("notify send error,logId = {}", logId, e);
            notifyMessageService.updateById(new NotifyMessageDO().setId(logId).setSent(true));
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMsgTask(CreateNotifyReqVO reqDTO) {
        log.info("create notify task {}", JSONUtil.toJsonStr(reqDTO));
        List<NotifyContentRespVO> notifyContentList = filterUser(reqDTO.getTemplateCode());
        NotifyTemplateDO template = getTemplate(reqDTO.getTemplateCode());
        List<NotifyMessageDO> notifyMessages = new ArrayList<>(notifyContentList.size());
        for (NotifyContentRespVO notifyContent : notifyContentList) {
            String key = notifyContent.getReceiverId() + "-" + reqDTO.getTemplateCode();
            if (Boolean.FALSE.equals(redisTemplate.boundValueOps(key).setIfAbsent(key, 60, TimeUnit.MINUTES))) {
                log.warn("重复发送通知，收信人id={},模板类型={}", notifyContent.getReceiverId(), reqDTO.getTemplateCode());
                continue;
            }
            NotifyMessageDO message = new NotifyMessageDO()
                    .setUserId(notifyContent.getReceiverId())
                    .setUserType(UserTypeEnum.ADMIN.getValue())
                    .setTemplateId(template.getId())
                    .setTemplateCode(template.getCode())
                    .setTemplateType(template.getType())
                    .setTemplateNickname(template.getNickname())
                    .setBatchCode(reqDTO.getBatchCode())
                    .setSent(false)
                    .setMediaTypes(template.getMediaTypes())
                    .setTemplateContent(notifyContent.getContent())
                    .setTemplateParams(notifyContent.getTemplateParams())
                    .setReadStatus(false);
            notifyMessages.add(message);
        }
        notifyMessageService.createMessageBatch(notifyMessages);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void triggerNotify(CreateNotifyReqVO reqDTO) {
        String batchCode = StringUtils.isBlank(reqDTO.getBatchCode()) ? "admin-sync" : reqDTO.getBatchCode();
        reqDTO.setBatchCode(batchCode);
        if (NotifyTemplateEnum.contains(reqDTO.getTemplateCode())) {
            createMsgTask(reqDTO);
            return;
        }
        Long logId = notifySendService.sendSingleNotify(reqDTO.getUserId(), reqDTO.getUserType(), batchCode, reqDTO.getTemplateCode(), reqDTO.getTemplateParams());
        sendNotify(logId);
    }

    @Override
    public List<NotifyContentRespVO> filterUser(String templateCode) {
        return notifyMediaFactory.getDataService(templateCode).filterNotifyContent();
    }

    @Override
    public Long createNotifyTemplate(NotifyTemplateCreateReqVO createReqVO) {
        if (CollectionUtils.isNotEmpty(createReqVO.getMediaTypes())) {
            for (Integer mediaType : createReqVO.getMediaTypes()) {
                notifyMediaFactory.getNotifyMedia(mediaType).valid(createReqVO);
            }
        }
        return templateService.createNotifyTemplate(createReqVO);
    }

    private void doSend(Long logId, Long userId, Integer mediaType, String content, Map<String, Object> params, String tempCode) {
        NotifyMediaAdapter notifyMediaAdapter = notifyMediaFactory.getNotifyMedia(mediaType);
        try {
            SendNotifyReqDTO sendNotifyReqDTO = new SendNotifyReqDTO()
                    .setContent(content)
                    .setUserId(userId)
                    .setParams(params)
                    .setTemplateCode(tempCode)
                    .setUserType(UserTypeEnum.ADMIN);
            SendNotifyResultDTO sendNotifyResultDTO = notifyMediaAdapter.sendNotify(sendNotifyReqDTO);
            notifyMediaAdapter.updateLog(logId, sendNotifyResultDTO);
        } catch (Exception e) {
            log.warn("send notify error,logId={},userId={},mediaType={}", logId, userId, mediaType, e);
            notifyMediaAdapter.updateLog(logId, SendNotifyResultDTO.error(null, e.getMessage()));
        }
    }

    @Override
    public void updateNotifyTemplate(NotifyTemplateUpdateReqVO updateReqVO) {
        if (CollectionUtils.isNotEmpty(updateReqVO.getMediaTypes())) {
            for (Integer mediaType : updateReqVO.getMediaTypes()) {
                notifyMediaFactory.getNotifyMedia(mediaType).valid(updateReqVO);
            }
        }
        templateService.updateNotifyTemplate(updateReqVO);
    }

    private NotifyTemplateDO getTemplate(String templateCode) {
        return notifyMediaFactory.getDataService(templateCode).getTemplate();
    }

}
