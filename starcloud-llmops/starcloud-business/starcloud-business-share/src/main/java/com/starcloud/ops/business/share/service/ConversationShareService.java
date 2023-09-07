package com.starcloud.ops.business.share.service;

import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareReq;
import com.starcloud.ops.business.share.controller.admin.vo.ConversationShareResp;

import java.util.List;

public interface ConversationShareService {

    /**
     * 创建分享链接
     *
     * @param req
     * @return
     */
    String createShareLink(ConversationShareReq req);

    /**
     * 修改分享记录
     *
     * @param req
     */
    void modifyRecord(ConversationShareReq req);

    /**
     * 分享的会话记录
     *
     * @param shareKey
     * @return
     */
    List<LogAppMessageRespVO> conversationDetail(String shareKey);

    /**
     * 分享记录
     *
     * @param conversationUid
     * @return
     */
    List<ConversationShareResp> shareRecord(String conversationUid);
}
