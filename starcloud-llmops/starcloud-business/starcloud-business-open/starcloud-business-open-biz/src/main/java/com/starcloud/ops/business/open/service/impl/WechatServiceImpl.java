package com.starcloud.ops.business.open.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.controller.admin.account.vo.MpAccountCreateReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.account.MpAccountDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.account.MpAccountService;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.app.api.channel.dto.BaseChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.dto.WeChatAccountChannelConfigDTO;
import com.starcloud.ops.business.app.api.channel.vo.request.AppPublishChannelReqVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.channel.AppPublishChannelEnum;
import com.starcloud.ops.business.app.exception.AppLimitException;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.open.api.dto.WeChatRequestDTO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WechatWebChannelReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WeChatBindReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WeChatBindRespVO;
import com.starcloud.ops.business.open.service.WechatService;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.service.MpAppManager;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import com.starcloud.ops.framework.common.api.enums.StateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.USER_BENEFITS_USELESS_INSUFFICIENT;
import static com.starcloud.ops.business.user.enums.DictTypeConstants.*;


@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private SocialUserService socialUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private SendUserMsgService sendUserMsgService;

    @Resource
    private ThreadWithContext threadWithContext;

    @Resource
    private EndUserServiceImpl endUserService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private AppLimitService appLimitService;

    @Resource
    @Lazy
    private MpAccountService mpAccountService;

    @Override
    public WeChatBindRespVO bindWxAccount(WeChatBindReqVO reqVO) {
        // 校验是否已绑定公共号
        validExist(reqVO.getAppId());

        List<DictDataDO> dictDataList = dictDataService.getDictDataList(WECHAT_APP);
        Map<String, List<DictDataDO>> dictMap = dictDataList.stream().collect(Collectors.groupingBy(DictDataDO::getLabel));
        DictDataDO callbackUrl = dictMap.get("callback_url").get(0);
        List<String> whitelist = dictMap.get("white_list").stream().map(DictDataDO::getValue).collect(Collectors.toList());
        WeChatBindRespVO weChatBindRespVO = new WeChatBindRespVO();
        weChatBindRespVO.setUrl(callbackUrl.getValue() + reqVO.getAppId());
        weChatBindRespVO.setToken(IdUtil.fastSimpleUUID());
        weChatBindRespVO.setWhitelist(whitelist);
        weChatBindRespVO.setEncryption(false);

        MpAccountDO mpAccountDO = mpAccountService.getAccountFromCache(reqVO.getAppId());
        if (mpAccountDO == null) {
            MpAccountCreateReqVO mpAccountCreateReqVO = new MpAccountCreateReqVO();
            mpAccountCreateReqVO.setAccount(reqVO.getAccount());
            mpAccountCreateReqVO.setName(reqVO.getName());
            mpAccountCreateReqVO.setToken(weChatBindRespVO.getToken());
            mpAccountCreateReqVO.setAppId(reqVO.getAppId());
            mpAccountCreateReqVO.setAppSecret(reqVO.getAppSecret());
            mpAccountCreateReqVO.setAesKey(weChatBindRespVO.getEncodingAesKey());
            Long accountId = mpAccountService.createAccount(mpAccountCreateReqVO);
            weChatBindRespVO.setMpAccountId(accountId);
        } else {
            weChatBindRespVO.setMpAccountId(mpAccountDO.getId());
            weChatBindRespVO.setToken(mpAccountDO.getToken());
        }
        AppPublishChannelReqVO channelReqVO = new AppPublishChannelReqVO();
        WeChatAccountChannelConfigDTO channelConfigDTO = new WeChatAccountChannelConfigDTO();
        channelConfigDTO.setWxAppId(reqVO.getAppId());
        channelConfigDTO.setName(reqVO.getName());
        channelConfigDTO.setAccount(reqVO.getAccount());
        channelConfigDTO.setAppSecret(reqVO.getAppSecret());
        channelConfigDTO.setToken(weChatBindRespVO.getToken());
        channelConfigDTO.setAccountId(weChatBindRespVO.getMpAccountId());
        channelConfigDTO.setUrl(weChatBindRespVO.getUrl());
        channelConfigDTO.setWhitelist(weChatBindRespVO.getWhitelist());

        channelReqVO.setName(reqVO.getName());
        channelReqVO.setStatus(StateEnum.ENABLE.getCode());
        channelReqVO.setAppUid(reqVO.getAppUid());
        channelReqVO.setPublishUid(reqVO.getPublishUid());
        channelReqVO.setType(AppPublishChannelEnum.WX_MP.getCode());
        channelReqVO.setConfig(channelConfigDTO);
        channelReqVO.setMediumUid(reqVO.getAppId());
        appPublishChannelService.create(channelReqVO);

        return weChatBindRespVO;
    }


    @Override
    public void asynReplyMsg(WeChatRequestDTO request) {
        String wxAppId = MpContextHolder.getAppId();

        AppPublishChannelRespVO channelRespVO = appPublishChannelService.getAllByMediumUid(wxAppId);
        if (channelRespVO == null) {
            sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), "此公众号未绑定机器人，请联系机器人管理员");
            log.info("wechat end");
            redisTemplate.delete(request.getFromUser() + "-ready");
            return;
        }

        if (channelRespVO.getStatus() == null || channelRespVO.getStatus() != 0) {
            sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), "此机器人已禁用，请联系机器人管理员启用");
            log.info("wechat end");
            redisTemplate.delete(request.getFromUser() + "-ready");
            return;
        }

        ChatRequestVO chatRequestVO = preChatRequest(request.getFromUser(), StringUtils.EMPTY, request.getQuery());

        // 限流
        AppLimitRequest limitRequest = AppLimitRequest.of(wxAppId, AppSceneEnum.MP.name(),
                chatRequestVO.getUserId() == null ? chatRequestVO.getEndUser() : String.valueOf(chatRequestVO.getUserId()));
        try {
            appLimitService.channelLimit(limitRequest);
        } catch (AppLimitException e) {
            sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), e.getMessage());
            log.info("wechat end");
            redisTemplate.delete(request.getFromUser() + "-ready");
            return;
        }

        // from_user + wx_appId  计算会话Uid
        chatRequestVO.setConversationUid(EncryptionUtils.calculateMD5UID(wxAppId + request.getFromUser()));
        chatRequestVO.setMediumUid(channelRespVO.getMediumUid());
        threadWithContext.asyncExecute(() -> {
            try {
                ChatAppEntity<ChatRequestVO, JsonData> appEntity = AppFactory.factory(chatRequestVO);
                JsonData execute = appEntity.execute(chatRequestVO);
                // 回复消息
                String msg = JSONUtil.parseObj(execute.getData()).getStr("text");
                if (StringUtils.isBlank(msg)) {
                    msg = JSONUtil.parseObj(execute.getData()).getJSONObject("returnValues").getStr("output");
                }
                if (StringUtils.isNotBlank(msg)) {
                    sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), msg);
                } else {
                    sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), "机器人繁忙!");
                }
            } catch (ServiceException e) {
                log.warn("execute error:", e);
                sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), e.getMessage());
            } catch (Exception e) {
                log.error("chat error", e);
                sendUserMsgService.sendWxMsg(wxAppId, request.getFromUser(), "AI 异常请稍后重试！");
            } finally {
                log.info("wechat end");
                redisTemplate.delete(request.getFromUser() + "-ready");
            }
        });
    }

    @Override
    public Boolean isInternalAccount(String wxAppId) {
        return wxAppId.equals(MpAppManager.getMpAppId(TenantContextHolder.getTenantId()));
    }

    @Override
    public List<MpAccountDO> getAccount(String appUid) {
        List<AppPublishChannelRespVO> channels = appPublishChannelService.getByAppUid(appUid);
        if (CollectionUtils.isEmpty(channels)) {
            return Collections.emptyList();
        }
        List<MpAccountDO> result = new ArrayList<>(channels.size());
        for (AppPublishChannelRespVO channel : channels) {
            BaseChannelConfigDTO config = channel.getConfig();
            if (config instanceof WeChatAccountChannelConfigDTO) {
                WeChatAccountChannelConfigDTO configDTO = (WeChatAccountChannelConfigDTO) config;
                MpAccountDO accountFromCache = mpAccountService.getAccountFromCache(configDTO.getWxAppId());
                result.add(accountFromCache);
            }
        }
        return result;
    }

    @Override
    public void delete(String uid) {
        AppPublishChannelRespVO publishChannelRespVO = appPublishChannelService.get(uid);
        if (publishChannelRespVO == null) {
            return;
        }
        appPublishChannelService.delete(uid);
        BaseChannelConfigDTO config = publishChannelRespVO.getConfig();
        if (config instanceof WeChatAccountChannelConfigDTO) {
            WeChatAccountChannelConfigDTO configDTO = (WeChatAccountChannelConfigDTO) config;
            mpAccountService.deleteAccount(configDTO.getAccountId());
        }
    }

    @Override
    public void modify(String uid, WeChatBindReqVO reqVO) {
        delete(uid);
        bindWxAccount(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createWebChannel(WechatWebChannelReqVO req) {
        Map<Integer, List<AppPublishChannelRespVO>> channelMap = appPublishChannelService.mapByAppPublishUidGroupByType(req.getPublishUid());
        List<AppPublishChannelRespVO> replyChannel = channelMap.get(AppPublishChannelEnum.WX_MP_REPLY.getCode());
        List<AppPublishChannelRespVO> menuChannel = channelMap.get(AppPublishChannelEnum.WX_MP_MENU.getCode());
        if (CollectionUtils.isEmpty(replyChannel)) {
            AppPublishChannelReqVO createReq = new AppPublishChannelReqVO();
            createReq.setName(req.getName() + AppPublishChannelEnum.WX_MP_REPLY.getLabel());
            createReq.setType(AppPublishChannelEnum.WX_MP_REPLY.getCode());
            createReq.setAppUid(req.getAppUid());
            createReq.setPublishUid(req.getPublishUid());
            createReq.setStatus(StateEnum.ENABLE.getCode());
            appPublishChannelService.create(createReq);
        }

        if (CollectionUtils.isEmpty(menuChannel)) {
            AppPublishChannelReqVO createReq = new AppPublishChannelReqVO();
            createReq.setName(req.getName() + AppPublishChannelEnum.WX_MP_MENU.getLabel());
            createReq.setType(AppPublishChannelEnum.WX_MP_MENU.getCode());
            createReq.setAppUid(req.getAppUid());
            createReq.setPublishUid(req.getPublishUid());
            createReq.setStatus(StateEnum.ENABLE.getCode());
            appPublishChannelService.create(createReq);
        }
    }

    private ChatRequestVO preChatRequest(String fromUser, String chatAppId, String query) {
        String appId = MpContextHolder.getAppId();
        ChatRequestVO chatRequestVO = new ChatRequestVO();
        chatRequestVO.setAppUid(chatAppId);
        chatRequestVO.setQuery(query);
        chatRequestVO.setScene(AppSceneEnum.MP.name());

        if (isInternalAccount(appId)) {
//            改为扣应用所有者权益
            AdminUserDO userDO = socialUserService.getSocialUser(fromUser, SocialTypeEnum.WECHAT_MP.getType(), UserTypeEnum.ADMIN.getValue());
//            chatRequestVO.setUserId(userDO.getId());
//            UserContextHolder.setUserId(userDO.getId());
            chatRequestVO.setEndUser(userDO.getId().toString());
        } else {
            String endUserId = endUserService.weMpLogin(appId + "-" + fromUser);
            chatRequestVO.setEndUser(endUserId);
        }
        return chatRequestVO;
    }


    private void validExist(String wxAppId) {
        AppPublishChannelRespVO appPublishChannelRespVO = appPublishChannelService.getAllByMediumUid(wxAppId);
        if (appPublishChannelRespVO != null) {
            throw new ServiceException(new ErrorCode(500, "此公共号已绑定发布渠道"));
        }
    }
}
