package com.starcloud.ops.business.app.convert;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.operate.request.AppOperateRequest;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.enums.AppResultCode;
import com.starcloud.ops.business.app.enums.AppOperateTypeEnum;
import com.starcloud.ops.business.app.exception.AppMarketException;

import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public class AppOperateConvert {

    /**
     * 转换为 TemplateOperateDO
     *
     * @param request 模版操作请求
     * @return TemplateOperateDO
     */
    public static AppOperateDO convert(AppOperateRequest request) {
        AppOperateDO operate = new AppOperateDO();
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (Objects.isNull(loginUserId)) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_OPERATE_FAIL, "User may not login");
        }
        operate.setUser(loginUserId.toString());
        operate.setTemplateUid(request.getTemplateUid());
        operate.setVersion(request.getVersion());
        AppOperateTypeEnum operateTypeEnum = AppOperateTypeEnum.getByName(request.getOperate().toUpperCase());
        if (Objects.isNull(operateTypeEnum)) {
            throw AppMarketException.exception(AppResultCode.TEMPLATE_MARKET_OPERATE_FAIL, "Operate type not Support " + request.getOperate());
        }
        operate.setOperate(request.getOperate());
        return operate;
    }
}
