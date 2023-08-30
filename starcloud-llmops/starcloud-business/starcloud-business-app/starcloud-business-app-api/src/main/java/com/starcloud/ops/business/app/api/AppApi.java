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

    AppRespVO get(String appUID);

}
