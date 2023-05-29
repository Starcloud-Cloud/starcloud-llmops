package com.starcloud.ops.business.app.api;

import com.starcloud.ops.business.app.api.dto.CreateTemplateRequestParams;
import com.starcloud.ops.business.app.api.dto.TemplatePageRequestParams;
import com.starcloud.ops.business.app.api.dto.TemplateRequestParams;
import com.starcloud.ops.business.app.api.dto.TemplateResult;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.framework.common.api.dto.ValObjDTO;

import java.util.Map;

public interface TemplateApi {

    /**
     * 分页查询我的模板
     *
     * @param params
     * @param code
     * @return
     */
    TemplateResult<PageResp<Object>> listMyTemplate(TemplatePageRequestParams params, String code);

    /**
     * 查询所有下载模板和推荐模板
     *
     * @return
     */
    TemplateResult<Map<String, Object>> ListDownloadRecommendTemplate();

    /**
     * 查询所有topic
     *
     * @return
     */
    TemplateResult<Map<String, Object>> listTopic();

    /**
     * 查询token明细
     *
     * @param code
     * @return
     */
    TemplateResult getCodeTokenInfo(String code);

    /**
     * 分页查询模板市场
     *
     * @param params
     */
    TemplateResult<PageResp> pageTemplateMarket(TemplatePageRequestParams params);

    /**
     * 检查用户是否已经安装模版
     *
     * @param params
     * @return
     */
    TemplateResult checkDownload(TemplateRequestParams params);

    /**
     * 模版市场模版操作, 点赞，浏览
     *
     * @param params
     * @return
     */
    TemplateResult<ValObjDTO> actionMarketTemplate(TemplateRequestParams params, String code);

    /**
     * 查看模板详情
     *
     * @param params
     * @return
     */
    TemplateResult detailMarketTemplate(TemplateRequestParams params);

    /**
     * 安装模板
     *
     * @param params
     * @return
     */
    TemplateResult<String> installTemplate(TemplateRequestParams params, String code);

    /**
     * 新建模板
     *
     * @param params
     * @return
     */
    TemplateResult<Integer> createTemplate(CreateTemplateRequestParams params, String code);
}
