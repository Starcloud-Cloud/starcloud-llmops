package com.starcloud.ops.business.app.convert.operate;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.validate.app.AppValidate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
@Mapper
public interface AppOperateConvert {

    AppOperateConvert INSTANCE = Mappers.getMapper(AppOperateConvert.class);

    /**
     * 转换为 TemplateOperateDO
     *
     * @param request 模版操作请求
     * @return TemplateOperateDO
     */
    default AppOperateDO convert(AppOperateReqVO request) {
        AppOperateDO operate = new AppOperateDO();
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        AppValidate.notNull(loginUserId, ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        operate.setUser(Long.toString(loginUserId));
        operate.setAppUid(request.getAppUid());
        operate.setVersion(request.getVersion());
        operate.setOperate(request.getOperate());
        return operate;
    }
}
