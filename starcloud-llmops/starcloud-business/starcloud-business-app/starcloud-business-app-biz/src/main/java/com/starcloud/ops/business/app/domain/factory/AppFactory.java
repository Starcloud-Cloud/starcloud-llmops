package com.starcloud.ops.business.app.domain.factory;

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

        return new AppEntity();
    }


    public static AppEntity factoryBase(String appId) {

        return new AppEntity();
    }

}
