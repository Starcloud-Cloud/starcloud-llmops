package com.starcloud.ops.business.log.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageAnnotationsDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageFeedbacksDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageSaveDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nacoyer
 */
@Configuration(proxyBeanMethods = false)
public class AppLogDeptPermissionConfiguration {

    @Bean("deptSpaceLogPermissionRuleCustomize")
    public DeptDataPermissionRuleCustomizer deptSpacePermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(LogAppConversationDO.class);
            rule.addDeptColumn(LogAppMessageDO.class);
            rule.addDeptColumn(LogAppMessageAnnotationsDO.class);
            rule.addDeptColumn(LogAppMessageFeedbacksDO.class);
            rule.addDeptColumn(LogAppMessageSaveDO.class);

        };
    }
}
