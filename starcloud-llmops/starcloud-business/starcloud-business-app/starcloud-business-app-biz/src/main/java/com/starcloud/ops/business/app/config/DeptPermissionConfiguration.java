package com.starcloud.ops.business.app.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DeptPermissionConfiguration {

    @Bean("deptSpacePermissionRuleCustomize")
    public DeptDataPermissionRuleCustomizer deptSpacePermissionRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(AppDO.class);
            rule.addDeptColumn(CreativePlanDO.class);
            rule.addDeptColumn(CreativePlanBatchDO.class);
            rule.addDeptColumn(CreativeContentDO.class);
            rule.addDeptColumn(PluginDefinitionDO.class);

        };
    }
}
