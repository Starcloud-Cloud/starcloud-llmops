package com.starcloud.ops.business.user.api;

public interface SendUserMsgService {

    /**
     * 公共号给用户发送消息公共号
     * 只支持 魔法ai 公共号
     *
     * @param userId
     * @param content
     */
    void sendMsgToWx(Long userId, String content);
}
