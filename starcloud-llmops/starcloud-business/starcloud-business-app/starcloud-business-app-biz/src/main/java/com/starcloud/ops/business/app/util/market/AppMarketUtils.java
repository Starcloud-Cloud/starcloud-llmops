package com.starcloud.ops.business.app.util.market;

import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.framework.common.api.util.StringUtil;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
public class AppMarketUtils {

    /**
     * 处理请求数据
     *
     * @param request 请求数据
     */
    public static void buildRequest(AppMarketReqVO request) {
        request.setName(request.getName().trim());
        request.setModel(request.getModel().toUpperCase().trim());
        request.setTags(StringUtil.toList(request.getTags()));
        request.setCategories(StringUtil.toList(request.getCategories()));
        request.setScenes(AppUtils.buildScenes(request.getScenes()));
        request.setImages(StringUtil.toList(request.getImages()));
    }
}
