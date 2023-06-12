package com.starcloud.ops.business.app.api;

import com.starcloud.ops.business.app.api.app.dto.AppDTO;

/**
 * 模版服务 API
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
public interface AppApi {

    /**
     * 根据模版 ID 获取模版信息
     *
     * @param id 模版 ID
     * @return 模版信息
     */
    AppDTO get(Long id);

}
