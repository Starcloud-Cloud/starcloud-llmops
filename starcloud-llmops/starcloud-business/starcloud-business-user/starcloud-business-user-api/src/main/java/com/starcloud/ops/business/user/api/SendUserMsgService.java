package com.starcloud.ops.business.user.api;

public interface SendUserMsgService {

    /**
     * 发送公共号消息
     *
     * @param appId  公共号id
     * @param openId 用户微信id
     * @param content 发送内容
     */
    void sendWxMsg(String appId, String openId, String content);

    /**
     * 公共号给用户发送消息公共号
     * 只支持 魔法ai 公共号
     *
     * @param userId
     * @param content
     */
    void sendMsgToWx(Long userId, String content);
}
