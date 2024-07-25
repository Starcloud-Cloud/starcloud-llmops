package com.starcloud.ops.business.app.service.plugins;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.ImageOcrReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.TextExtractionReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.XhsOcrReqVO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ImageOcrActionHandler;

import java.util.HashMap;
import java.util.Map;

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


    /**
     * 临时使用，以后自行创建MAP
     * @param params
     * @return
     */
    @Deprecated
    default Map<String, Object> toParamsMap(Object params) {

        Map<String, Object> map = BeanUtil.beanToMap(params);
        return map;
    }
}
