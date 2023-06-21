package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;

/**
 * 获取步骤处理器工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public class AppFactory {

    /**
     * AppRepository
     */
    private static AppRepository appRepository;

    /**
     * 获取 AppRepository
     *
     * @return AppRepository
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    public static AppEntity factory(String appId) {
        return getAppRepository().getByUid(appId);
    }


    public static AppEntity factory(String appId, AppReqVO appRequest) {

        AppEntity app = new AppEntity();
        Assert.notNull(app, "app fire is fail, app[{0}] not found", appId);

        return app;
    }


    public static AppEntity factory(String appId, AppReqVO appRequest, String requestId) {
        return null;
    }
}
