package com.starcloud.ops.business.app.api;

import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;

/**
 * 应用服务 API
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
public interface AppApi {

    /**
     * 获取应用信息
     * @param appUid 应用 UID
     * @return 应用信息
     */
    AppRespVO get(String appUid);

    /**
     * 获取应用信息-简单
     * @param appUid 应用 UID
     * @return 应用信息
     */
    AppRespVO getSimple(String appUid);

}
