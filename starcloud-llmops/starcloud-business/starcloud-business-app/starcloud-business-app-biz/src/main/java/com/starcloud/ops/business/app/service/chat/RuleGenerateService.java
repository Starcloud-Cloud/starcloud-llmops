package com.starcloud.ops.business.app.service.chat;

import com.starcloud.ops.business.app.api.app.vo.response.RuleGenerateRespVO;
import com.starcloud.ops.business.app.api.chat.RuleGenerateRequest;

/**
 * @author starcloud
 */
public interface RuleGenerateService {

    /**
     * 自动编排聊天应用
     * @param request
     * @return
     */
    RuleGenerateRespVO generateRule(RuleGenerateRequest request);
}
