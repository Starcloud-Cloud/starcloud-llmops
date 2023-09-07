package com.starcloud.ops.business.share.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageExportReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareReq;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareResp;
import com.starcloud.ops.business.share.convert.ConversationShareConvert;
import com.starcloud.ops.business.share.dal.dataobject.ShareConversationDO;
import com.starcloud.ops.business.share.dal.mysql.ShareConversationMapper;
import com.starcloud.ops.business.share.service.ConversationShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public String createShareLink(ConversationShareReq req) {
        LogAppConversationDO appConversation = conversationService.getAppConversation(req.getConversationUid());
        if (appConversation == null) {
            throw exception(APP_CONVERSATION_NOT_EXISTS_UID, req.getConversationUid());
        }
        String shareKey =  RandomStringUtils.random(8, Boolean.TRUE, Boolean.TRUE);
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        ShareConversationDO shareConversationDO = new ShareConversationDO();
        shareConversationDO.setConversationUid(req.getConversationUid());
        shareConversationDO.setDisabled(false);
        shareConversationDO.setEndUser(loginUserId == null);

        shareConversationDO.setExpiresTime(LocalDateTime.now().plusDays(req.getExpiresTime() == null ? 1 : req.getExpiresTime() ));
        shareConversationDO.setUid(IdUtil.fastSimpleUUID());
        shareConversationDO.setShareKey(shareKey);
        shareConversationDO.setAppUid(appConversation.getAppUid());
        shareConversationMapper.insert(shareConversationDO);
        return shareKey;
    }

    @Override
    public void modifyRecord(ConversationShareReq req) {
        if (StringUtils.isBlank(req.getUid())) {
            throw exception(new ErrorCode(500, "uid 不能为空"));
        }
        shareConversationMapper.modify(req);
    }

    @Override
    public List<LogAppMessageRespVO> conversationDetail(String shareKey) {
        ShareConversationDO shareConversationDO = shareConversationMapper.getByKey(shareKey);
        if (shareConversationDO.getDisabled()) {
            throw exception(new ErrorCode(500, "分享的链接已禁用"));
        }

        if (LocalDateTime.now().compareTo(shareConversationDO.getExpiresTime()) > 0) {
            throw exception(new ErrorCode(500, "分享的链接已过期"));
        }
        LogAppMessageExportReqVO exportReqVO = new LogAppMessageExportReqVO();
        exportReqVO.setAppUid(shareConversationDO.getAppUid());
        exportReqVO.setAppConversationUid(shareConversationDO.getConversationUid());
        List<LogAppMessageDO> appMessageList = messageService.getAppMessageList(exportReqVO);
        appMessageList = appMessageList.stream().filter(logAppMessageDO -> logAppMessageDO.getCreateTime().compareTo(shareConversationDO.getCreateTime()) < 0).collect(Collectors.toList());
        return LogAppMessageConvert.INSTANCE.convertList(appMessageList);
    }

    @Override
    public List<ConversationShareResp> shareRecord(String conversationUid) {
        List<ShareConversationDO> shareConversationDOS = shareConversationMapper.selectList(conversationUid);
        return ConversationShareConvert.INSTANCE.convert(shareConversationDOS);
    }
}
