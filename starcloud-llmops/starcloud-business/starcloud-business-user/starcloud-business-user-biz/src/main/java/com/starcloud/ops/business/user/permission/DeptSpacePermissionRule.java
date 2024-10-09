package com.starcloud.ops.business.user.permission;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRule;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.response.UserDeptRespVO;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
//@Component
public class DeptSpacePermissionRule implements DataPermissionRule {

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private UserDeptService userDeptService;

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

        List<UserDeptRespVO> userDeptRespList = userDeptService.deptList();
        List<Expression> deptIds = userDeptRespList.stream().map(UserDeptRespVO::getDeptId).map(LongValue::new).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(deptIds)) {
            AdminUserRespDTO user = adminUserApi.getUser(userId);
            deptIds.add(new LongValue(user.getDeptId()));
        }

        return new InExpression(MyBatisUtils.buildColumn(tableName, tableAlias, DEPT_COLUMN),
                new ExpressionList(deptIds));
    }
}
