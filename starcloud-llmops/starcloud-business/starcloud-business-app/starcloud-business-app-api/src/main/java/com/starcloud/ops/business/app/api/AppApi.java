package com.starcloud.ops.business.app.api;

import com.starcloud.ops.business.app.api.app.dto.AppDTO;

/**
 * 应用服务 API
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
public interface AppApi {

    /**
     * 根据应用 ID 获取应用信息
     *
     * @param id 应用 ID
     * @return 应用信息
     */
    AppDTO get(Long id);

}
