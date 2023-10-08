package com.starcloud.ops.business.open.service;

import com.starcloud.ops.business.open.api.dto.WeChatRequestDTO;
import com.starcloud.ops.business.open.controller.admin.vo.request.WeChatBindReqVO;
import com.starcloud.ops.business.open.controller.admin.vo.response.WeChatBindRespVO;

public interface WechatService {


    /**
     * 绑定企业微信
     * @param reqVO
     */
    WeChatBindRespVO bindWxAccount(WeChatBindReqVO reqVO);

    /**
     * 异步回复
     *
     * @param chatRequestDTO
     */
    void asynReplyMsg(WeChatRequestDTO chatRequestDTO);

    /**
     * 是否是内部帐号
     * @param wxAppId
     * @return
     */
    Boolean isInternalAccount(String wxAppId);
}
