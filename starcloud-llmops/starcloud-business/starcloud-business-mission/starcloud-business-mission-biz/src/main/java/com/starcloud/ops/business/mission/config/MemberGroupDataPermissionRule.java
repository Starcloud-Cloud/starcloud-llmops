package com.starcloud.ops.business.mission.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRule;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.mission.api.WechatUserBindService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Slf4j
@Component
public class MemberGroupDataPermissionRule implements DataPermissionRule {

    @Resource
    private WechatUserBindService wechatUserBindService;

    @Override
    public Set<String> getTableNames() {
        return CollectionUtil.newHashSet( "llm_notification","llm_single_mission");
    }

    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        if (!UserTypeEnum.MEMBER.getValue().equals(WebFrameworkUtils.getLoginUserType())) {
            return null;
        }
        return new InExpression(MyBatisUtils.buildColumn(tableName, tableAlias, "creator"),
                new ExpressionList(java.util.Collections.singletonList(new StringValue(wechatUserBindService.getBindUser()))));
    }
}
