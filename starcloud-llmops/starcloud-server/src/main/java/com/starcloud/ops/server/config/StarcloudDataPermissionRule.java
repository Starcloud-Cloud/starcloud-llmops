package com.starcloud.ops.server.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.DataPermissionRule;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.springframework.stereotype.Component;

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
            "llm_app",
            "llm_app_publish",
            "llm_app_publish_channel",
            "llm_log_app_conversation",
            "llm_log_app_message",
            "llm_log_app_message_annotations",
            "llm_log_app_message_feedbacks",
            "llm_log_app_message_save"
    );

    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return null;
        }
        return new EqualsTo(MyBatisUtils.buildColumn(tableName, tableAlias, "creator"), new LongValue(userId));
    }

}
