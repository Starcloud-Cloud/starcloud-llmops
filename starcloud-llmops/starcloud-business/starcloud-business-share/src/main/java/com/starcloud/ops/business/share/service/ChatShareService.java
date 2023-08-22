package com.starcloud.ops.business.share.service;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.share.controller.app.vo.ChatDetailReqVO;

import java.util.Map;

public interface ChatShareService {

    /**
     * 已发布明细
     *
     * @param mediumUid
     * @return
     */
    AppRespVO chatShareDetail(String mediumUid);


    /**
     * 应用详情
     *
     * @param reqVO
     * @return
     */
    Map<String, AppRespVO> detailList(ChatDetailReqVO reqVO);

    /**
     * 分享聊天
     *
     * @param chatRequestVO
     */
    void shareChat(ChatRequestVO chatRequestVO);
}
