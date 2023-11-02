package com.starcloud.ops.business.app.service.xhs;

import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteResponse;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
public interface XhsService {

    /**
     * 获取应用信息
     *
     * @param uid 应用UID
     * @return 应用信息
     */
    XhsAppResponse getApp(String uid);

    /**
     * 执行
     *
     * @param request 请求
     * @return 响应
     */
    XhsExecuteResponse execute(XhsExecuteRequest request);
}
