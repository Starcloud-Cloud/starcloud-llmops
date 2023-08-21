package com.starcloud.ops.business.share.service.impl;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.share.service.AppShareService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-21
 */
@Slf4j
@Service
public class AppShareServiceImpl implements AppShareService {

    @Resource
    private AppService appService;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    /**
     * 根据 mediumUid 获取应用详情
     *
     * @param mediumUid 应用唯一标识
     * @return 应用详情
     */
    @Override
    public AppRespVO appShareDetail(String mediumUid) {
        return appPublishChannelService.getAppByMediumUid(mediumUid);
    }

    /**
     * 执行分享应用
     *
     * @param executeRequest 执行应用请求参数
     */
    @Override
    public void shareAppExecute(AppExecuteReqVO executeRequest) {
        log.info("分享应用执行参数：endUser: {}, mediumUid: {}, scene: {}, step: {}",
                executeRequest.getEndUser(), executeRequest.getMediumUid(), executeRequest.getScene(), executeRequest.getStepId());
        // 执行应用
        appService.asyncExecute(executeRequest);


    }
}
