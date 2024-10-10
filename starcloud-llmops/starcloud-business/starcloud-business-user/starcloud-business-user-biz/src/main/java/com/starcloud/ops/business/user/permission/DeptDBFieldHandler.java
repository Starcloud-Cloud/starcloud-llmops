package com.starcloud.ops.business.user.permission;

import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.mybatis.core.handler.DefaultDBFieldHandler;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class DeptDBFieldHandler extends DefaultDBFieldHandler {

    @Resource
    @Lazy
    private AdminUserApi adminUserApi;

    private static final String DEPT_ID = "deptId";

    @Override
    @DataPermission(enable = false)
    public void insertFill(MetaObject metaObject) {
        super.insertFill(metaObject);
        if (Objects.isNull(metaObject)
                || Objects.isNull(metaObject.getOriginalObject())) {
            return;
        }
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (Objects.isNull(loginUser)
                || Objects.equals(loginUser.getUserType(), UserTypeEnum.MEMBER.getValue())) {
            return;
        }

        if (!DeptPermissionTable.getTableNames().contains(findTableInfo(metaObject).getTableName())) {
            return;
        }
        Object obj = metaObject.getOriginalObject();
        if (!(obj instanceof DeptBaseDO)) {
            return;
        }

        Long userId = loginUser.getId();
        if (Objects.isNull(userId)) {
            userId = UserContextHolder.getUserId();
        }

        if (Objects.isNull(userId)) {
            return;
        }
        AdminUserRespDTO user = adminUserApi.getUser(userId);

        // deptId为空时填充
        strictInsertFill(metaObject, DEPT_ID, Long.class, user.getDeptId());
    }

}
