package com.starcloud.ops.business.app.service.xhs;

import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteResponse;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
public interface XhsService {

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    List<XhsImageTemplateDTO> imageTemplates();

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
