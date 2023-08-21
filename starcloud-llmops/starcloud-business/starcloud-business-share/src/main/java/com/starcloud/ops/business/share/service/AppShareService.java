package com.starcloud.ops.business.share.service;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-21
 */
public interface AppShareService {

    /**
     * 根据 mediumUid 获取应用详情
     *
     * @param mediumUid 应用唯一标识
     * @return 应用详情
     */
    AppRespVO appShareDetail(String mediumUid);

    /**
     * 执行分享应用
     *
     * @param executeRequest 执行应用请求参数
     */
    void shareAppExecute(@RequestBody AppExecuteReqVO executeRequest);
}
