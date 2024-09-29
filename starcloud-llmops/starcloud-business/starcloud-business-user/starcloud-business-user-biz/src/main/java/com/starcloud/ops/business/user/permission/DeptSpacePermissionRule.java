package com.starcloud.ops.business.user.permission;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRule;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class DeptSpacePermissionRule implements DataPermissionRule {

    @Resource
    private AdminUserApi adminUserApi;

    private static final String DEPT_COLUMN = "dept_id";

    @Override
    public Set<String> getTableNames() {
        return DeptPermissionTable.getTableNames();
    }

    @Override
    @DataPermission(enable = false)
    public Expression getExpression(String tableName, Alias tableAlias) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        // 小程序单独走权限
        if (Objects.isNull(userId) || Objects.equals(WebFrameworkUtils.getLoginUserType(), UserTypeEnum.MEMBER.getValue())) {
            return null;
        }
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        return new EqualsTo(MyBatisUtils.buildColumn(tableName, tableAlias, DEPT_COLUMN), new LongValue(user.getDeptId()));
    }
}
