package com.starcloud.ops.business.user.service;

import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.QrCodeTicketVO;
import com.starcloud.ops.business.user.pojo.request.ScanLoginRequest;

public interface WeChatService {

    /**
     * 获取公共号二维码
     * @return
     */
    QrCodeTicketVO qrCodeCreate(String inviteCode);

    /**
     * 获取授权用户Id
     * @param
     * @return
     */
    Long authUser(ScanLoginRequest request);

    /**
     * 创建 Token 令牌，记录登录日志
     * @return
     */
    AuthLoginRespVO createTokenAfterLoginSuccess(Long userId);
}
