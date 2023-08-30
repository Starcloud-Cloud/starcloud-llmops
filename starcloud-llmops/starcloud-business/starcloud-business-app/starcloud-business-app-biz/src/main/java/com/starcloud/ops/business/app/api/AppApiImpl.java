package com.starcloud.ops.business.app.api;


import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.service.app.AppService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 模版服务 API 实现
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@Slf4j
@Service
public class AppApiImpl implements AppApi {

    @Resource
    private AppService appService;

    /**
     * 获取应用信息
     *
     * @param appUid 应用 UID
     * @return 应用信息
     */
    @Override
    public AppRespVO get(String appUid) {
        return appService.get(appUid);
    }

    /**
     * 获取应用信息-简单
     *
     * @param appUid 应用 UID
     * @return 应用信息
     */
    @Override
    public AppRespVO getSimple(String appUid) {
        return appService.getSimple(appUid);
    }
}
