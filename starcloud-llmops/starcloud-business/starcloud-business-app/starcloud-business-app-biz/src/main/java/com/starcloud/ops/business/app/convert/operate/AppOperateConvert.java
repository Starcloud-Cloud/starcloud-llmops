package com.starcloud.ops.business.app.convert.operate;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.operate.request.AppOperateRequest;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.enums.operate.AppOperateTypeEnum;

import com.starcloud.ops.business.app.enums.ErrorCodeConstants;

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
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_FAIL, "User may not login");
        }
        operate.setUser(loginUserId.toString());
        operate.setAppUid(request.getAppUid());
        operate.setVersion(request.getVersion());
        AppOperateTypeEnum operateTypeEnum = AppOperateTypeEnum.getByName(request.getOperate().toUpperCase());
        if (Objects.isNull(operateTypeEnum)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_MARKET_OPERATE_NOT_SUPPORTED, request.getOperate());
        }
        operate.setOperate(request.getOperate());
        return operate;
    }
}
