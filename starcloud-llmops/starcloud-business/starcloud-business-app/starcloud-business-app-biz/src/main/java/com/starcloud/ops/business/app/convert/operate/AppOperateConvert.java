package com.starcloud.ops.business.app.convert.operate;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
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
        if (loginUserId == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        operate.setUser(Long.toString(loginUserId));
        operate.setAppUid(request.getAppUid());
        operate.setVersion(request.getVersion());
        operate.setOperate(request.getOperate());
        operate.setDeleted(Boolean.FALSE);
        return operate;
    }

    /**
     * 转换为 TemplateOperateDO
     *
     * @param uid         应用 uid
     * @param version     版本号
     * @param operateType 操作类型
     * @return TemplateOperateDO
     */
    default AppOperateDO convert(String uid, Integer version, String operateType, String userId) {
        AppOperateDO operate = new AppOperateDO();
        operate.setUser(userId);
        operate.setAppUid(uid);
        operate.setVersion(version);
        operate.setOperate(operateType);
        operate.setDeleted(Boolean.FALSE);
        return operate;
    }
}
