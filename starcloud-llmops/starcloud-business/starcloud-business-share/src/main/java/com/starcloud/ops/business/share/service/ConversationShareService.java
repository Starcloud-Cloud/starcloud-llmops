package com.starcloud.ops.business.share.service;

import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import com.starcloud.ops.business.share.controller.admin.vo.AppDetailRespVO;
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
    ConversationShareResp createShareLink(ConversationShareReq req);

    /**
     * 修改分享记录
     *
     * @param req
     */
    void modifyRecord(ConversationShareReq req);

    /**
     * 删除分享记录
     *
     * @param appUid
     */
    void deleteShare(String appUid);

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

    /**
     * 获取分享应用详情
     *
     * @param shareKey
     * @return
     */
    AppDetailRespVO appDetail(String shareKey);

    /**
     * 风险记录详情
     *
     * @param shareKey
     * @return
     */
    ConversationShareResp recordDetail(String shareKey);
}
