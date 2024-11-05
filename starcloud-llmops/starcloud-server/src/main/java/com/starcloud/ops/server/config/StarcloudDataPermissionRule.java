package com.starcloud.ops.server.config;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRule;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.RoleApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import com.google.common.collect.Sets;
import com.starcloud.ops.business.user.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author nacoyer
 */
@Slf4j
@Component // 声明为 Spring Bean，保证被 yudao-spring-boot-starter-biz-data-permission 组件扫描到
public class StarcloudDataPermissionRule implements DataPermissionRule {

    /**
     * 需要进行数据权限过滤的表名
     */
    private static final Set<String> TABLE_NAMES = Sets.newHashSet(
            "llm_app_publish",
            "llm_app_publish_channel",
            "listing_dict",
            "listing_draft",
            "listing_keyword_bind"
    );

    @Resource
    private final PermissionApi permissionApi;

    @Resource
    private final RoleApi roleApi;

    public StarcloudDataPermissionRule(PermissionApi permissionApi, RoleApi roleApi) {
        this.permissionApi = permissionApi;
        this.roleApi = roleApi;
    }

    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        Long userId = WebFrameworkUtils.getLoginUserId();

        try {
            if (Objects.equals(WebFrameworkUtils.getLoginUserType(), UserTypeEnum.MEMBER.getValue())) {
                return null;
            }
        } catch (Exception e) {
            log.warn("check user error", e.getMessage());
        }

        if (userId == null) {
            return null;
        }

        List<String> roleCodeList = roleApi.getRoleCodeList(userId);
        if (roleCodeList.contains(UserUtils.MOFAAI_APP_ADMIN) || roleCodeList.contains(UserUtils.ADMIN_ROLE)) {
            return null;
        }

        DeptDataPermissionRespDTO deptDataPermission = permissionApi.getDeptDataPermission(userId);
        // 超级管理员，不进行数据权限过滤
        if (deptDataPermission.getAll()) {
            return null;
        }
        // 普通用户，只能查询自己创建的数据
        return new EqualsTo(MyBatisUtils.buildColumn(tableName, tableAlias, "creator"), new LongValue(userId));
    }

}
