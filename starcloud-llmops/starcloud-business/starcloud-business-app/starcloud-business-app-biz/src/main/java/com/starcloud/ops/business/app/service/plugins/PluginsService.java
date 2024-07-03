package com.starcloud.ops.business.app.service.plugins;

import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.ImageOcrReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.TextExtractionReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.XhsOcrReqVO;

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
    OcrGeneralDTO imageOcr(ImageOcrReqVO reqVO);

    /**
     * 文本智能提取
     *
     * @param reqVO
     * @return
     */
    JSONObject intelligentTextExtraction(TextExtractionReqVO reqVO);
}
