package com.starcloud.ops.business.app.convert.operate;

import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.dal.databoject.operate.AppOperateDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

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
        operate.setAppUid(request.getAppUid());
        operate.setVersion(request.getVersion());
        operate.setOperate(request.getOperate());
        operate.setUser(request.getUserId());
        operate.setCreator(request.getUserId());
        operate.setUpdater(request.getUserId());
        operate.setCreateTime(LocalDateTime.now());
        operate.setUpdateTime(LocalDateTime.now());
        operate.setDeleted(Boolean.FALSE);
        operate.setTenantId(request.getTenantId());
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
