package com.starcloud.ops.business.app.validate.market;

import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.validate.app.AppValidate;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
public class AppMarketValidate {

    public static void validate(AppMarketReqVO request) {
        AppValidate.assertNotNull(request, "App Market Request Data");

    }
}
