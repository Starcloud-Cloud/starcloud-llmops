package com.starcloud.ops.business.poster.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DeptPermissionPosterConfiguration {

    @Bean("deptSpacePermissionPosterRuleCustomize")
    public DeptDataPermissionRuleCustomizer deptSpacePermissionPosterRuleCustomizer() {
        return rule -> {
            // dept
            rule.addDeptColumn(MaterialGroupDO.class);
            rule.addDeptColumn(MaterialDO.class);
        };
    }
}
