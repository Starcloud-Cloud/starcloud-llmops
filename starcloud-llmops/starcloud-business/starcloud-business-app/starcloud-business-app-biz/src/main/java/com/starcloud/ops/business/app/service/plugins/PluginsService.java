package com.starcloud.ops.business.app.service.plugins;

import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;

public interface PluginsService {

    /**
     * 小红书爬取 ocr
     *
     * @param reqVO
     * @return
     */
    XhsNoteDTO xhsOcr(XhsOcrReqVO reqVO);

    /**
     * 图片ocr
     *
     * @param reqVO
     * @return
     */
    HandlerResponse imageOcr(ImageOcrReqVO reqVO);

    /**
     * 文本智能提取
     *
     * @param reqVO
     * @return
     */
    JSONObject intelligentTextExtraction(TextExtractionReqVO reqVO);


    String executePlugin(PluginExecuteReqVO reqVO);

    PluginExecuteRespVO getPluginResult(PluginResultReqVO pluginResultReqVO);

    /**
     * 同步执行插件
     * @param reqVO
     */
    Object syncExecute(PluginExecuteReqVO reqVO);

    /**
     * 验证插件
     */
    String verify(PluginTestReqVO reqVO);

    /**
     * 验证结果
     */
    VerifyResult verifyResult(PluginTestResultReqVO resultReqVO);
}
