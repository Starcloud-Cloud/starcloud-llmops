package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;

/**
 * 获取步骤处理器工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public class AppFactory {


    public static AppEntity factory(String appId) {
        AppEntity app = new AppEntity();
        app.setUid(appId);
        app = app.getByUid();
        Assert.notNull(app, "app fire is fail, app[{0}] not found", appId);
        return app;
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
