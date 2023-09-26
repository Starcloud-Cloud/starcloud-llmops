package com.starcloud.ops.business.share.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageExportReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.business.share.controller.admin.vo.AppDetailRespVO;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareReq;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareResp;
import com.starcloud.ops.business.share.convert.ChatAppConvert;
import com.starcloud.ops.business.share.convert.ConversationShareConvert;
import com.starcloud.ops.business.share.dal.dataobject.ShareConversationDO;
import com.starcloud.ops.business.share.dal.mysql.ShareConversationMapper;
import com.starcloud.ops.business.share.service.ConversationShareService;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.log.enums.ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID;

@Slf4j
@Service
public class ConversationShareServiceImpl implements ConversationShareService {

    @Resource
    private ShareConversationMapper shareConversationMapper;

    @Resource
    private LogAppConversationService conversationService;

    @Resource
    private LogAppMessageService messageService;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppPublishService appPublishService;

    @Resource
    private AppService appService;

    @Override
    public ConversationShareResp createShareLink(ConversationShareReq req) {
        LogAppConversationDO appConversation = conversationService.getAppLogConversation(req.getConversationUid());
        if (appConversation == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS_UID, req.getConversationUid());
        }

        String appUid = appConversation.getAppUid();
        String inviteCode, mediumUid = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(req.getMediumUid())) {
            mediumUid = req.getMediumUid();
        } else {
            if (AppSceneEnum.CHAT_MARKET.name().equals(req.getScene())) {
                AppMarketRespVO appMarketRespVO = appMarketService.get(appConversation.getAppUid());
                AppPublishRespVO appPublishRespVO = appPublishService.getMarket(appMarketRespVO.getUid());
                appUid = appPublishRespVO.getAppUid();
            }

            List<AppPublishChannelRespVO> appPublishChannelRespVOS = appPublishChannelService.getByAppUid(appUid);
            Optional<AppPublishChannelRespVO> channelOptional = appPublishChannelRespVOS.stream().filter(channelRespVO -> AppPublishChannelEnum.SHARE_LINK.getCode().equals(channelRespVO.getType())).findFirst();
            if (channelOptional.isPresent()) {
                mediumUid = channelOptional.get().getMediumUid();
            }
        }

        if (StringUtils.isBlank(req.getInviteCode())) {
            Long userId = WebFrameworkUtils.getLoginUserId();
            if (AppSceneEnum.CHAT_MARKET.name().equals(req.getScene())) {
                inviteCode = EncryptionUtils.encrypt(userId).toString();
            } else if (AppSceneEnum.CHAT_TEST.name().equals(req.getScene())) {
                AppRespVO appRespVO = appService.get(appUid);
                inviteCode = EncryptionUtils.encrypt(Long.valueOf(appRespVO.getCreator())).toString();
            } else if (AppSceneEnum.SHARE_WEB.name().equals(req.getScene())) {
                AppRespVO appRespVO = appService.get(appUid);
                inviteCode = EncryptionUtils.encrypt(Long.valueOf(appRespVO.getCreator())).toString();
            } else if (AppSceneEnum.SHARE_JS.name().equals(req.getScene())) {
                AppRespVO appRespVO = appService.get(appUid);
                inviteCode = EncryptionUtils.encrypt(Long.valueOf(appRespVO.getCreator())).toString();
            } else if (AppSceneEnum.SHARE_IFRAME.name().equals(req.getScene())) {
                AppRespVO appRespVO = appService.get(appUid);
                inviteCode = EncryptionUtils.encrypt(Long.valueOf(appRespVO.getCreator())).toString();
            } else {
                throw exception(new ErrorCode(500, "错误的场景值"));
            }
        } else {
            inviteCode = req.getInviteCode();
        }

        String shareKey = RandomStringUtils.random(8, Boolean.TRUE, Boolean.TRUE);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        ShareConversationDO shareConversationDO = new ShareConversationDO();
        shareConversationDO.setConversationUid(req.getConversationUid());
        shareConversationDO.setDisabled(false);
        shareConversationDO.setEndUser(loginUserId == null);
        shareConversationDO.setExpiresTime(LocalDateTime.now().plusDays(req.getExpiresTime() == null ? 3 : req.getExpiresTime()));
        shareConversationDO.setUid(IdUtil.fastSimpleUUID());
        shareConversationDO.setShareKey(shareKey);
        shareConversationDO.setAppUid(appConversation.getAppUid());
        shareConversationDO.setMediumUid(mediumUid);
        shareConversationDO.setInviteCode(inviteCode);
        shareConversationDO.setScene(req.getScene());
        shareConversationMapper.insert(shareConversationDO);
        return ConversationShareConvert.INSTANCE.convert(shareConversationDO);
    }

    @Override
    public void modifyRecord(ConversationShareReq req) {
        if (StringUtils.isBlank(req.getUid())) {
            throw exception(new ErrorCode(500, "uid 不能为空"));
        }
        shareConversationMapper.modify(req);
    }

    @Override
    public void deleteShare(String appUid) {
        Assert.notBlank(appUid,"删除数据失败，appUid为空");
        shareConversationMapper.delete(appUid);
    }

    @Override
    public List<LogAppMessageRespVO> conversationDetail(String shareKey) {
        ShareConversationDO shareConversationDO = getShareDO(shareKey);
        LogAppMessageExportReqVO exportReqVO = new LogAppMessageExportReqVO();
        exportReqVO.setAppUid(shareConversationDO.getAppUid());
        exportReqVO.setAppConversationUid(shareConversationDO.getConversationUid());
        List<LogAppMessageDO> appMessageList = messageService.listAppLogMessage(exportReqVO);
        appMessageList = appMessageList.stream().filter(logAppMessageDO -> logAppMessageDO.getCreateTime().isBefore(shareConversationDO.getCreateTime())).collect(Collectors.toList());
        Collections.reverse(appMessageList);
        return LogAppMessageConvert.INSTANCE.convertList(appMessageList);
    }

    @Override
    public List<ConversationShareResp> shareRecord(String conversationUid) {
        List<ShareConversationDO> shareConversationDOS = shareConversationMapper.selectList(conversationUid);
        return ConversationShareConvert.INSTANCE.convert(shareConversationDOS);
    }

    @Override
    public AppDetailRespVO appDetail(String shareKey) {
        ShareConversationDO shareConversationDO = getShareDO(shareKey);
        if (StringUtils.isNotBlank(shareConversationDO.getMediumUid())) {
            // 渠道分享
            ChatAppEntity appEntity = AppFactory.factory(shareConversationDO.getMediumUid());
            return ChatAppConvert.INSTANCE.convert(appEntity);
        }
        LogAppConversationDO appConversation = conversationService.getAppLogConversation(shareConversationDO.getConversationUid());
        if (appConversation == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS_UID, shareConversationDO.getConversationUid());
        }

        if (AppSceneEnum.CHAT_MARKET.name().equals(appConversation.getFromScene())) {
            // 应用市场场景
            ChatAppEntity appEntity = AppFactory.factroyMarket(appConversation.getAppUid());
            return ChatAppConvert.INSTANCE.convert(appEntity);
        }
        // 我的应用
        ChatAppEntity appEntity = AppFactory.factoryChatApp(appConversation.getAppUid());
        return ChatAppConvert.INSTANCE.convert(appEntity);
    }

    private ShareConversationDO getShareDO(String shareKey) {
        ShareConversationDO shareConversationDO = shareConversationMapper.getByKey(shareKey);
        if (shareConversationDO == null || shareConversationDO.getDisabled()) {
            throw exception(new ErrorCode(500, "分享的链接已禁用"));
        }

        if (LocalDateTime.now().isAfter(shareConversationDO.getExpiresTime())) {
            throw exception(new ErrorCode(500, "分享的链接已过期"));
        }
        return shareConversationDO;
    }

    @Override
    public ConversationShareResp recordDetail(String shareKey) {
        ShareConversationDO shareConversationDO = shareConversationMapper.getByKey(shareKey);
        return ConversationShareConvert.INSTANCE.convert(shareConversationDO);
    }
}
