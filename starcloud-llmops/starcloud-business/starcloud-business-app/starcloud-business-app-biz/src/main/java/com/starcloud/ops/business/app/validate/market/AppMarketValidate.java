package com.starcloud.ops.business.app.validate.market;

import com.starcloud.ops.business.app.api.market.request.AppMarketRequest;
import com.starcloud.ops.business.app.validate.app.AppValidate;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
public class AppMarketValidate {

    public static void validate(AppMarketRequest request) {
        AppValidate.assertNotNull(request, "App Market Request Data");

    }
}
