package com.starcloud.ops.business.mission.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MissionPermissionConfiguration {

    @Bean("missionPermissionRuleCustomize")
    public DeptDataPermissionRuleCustomizer missionPermissionRuleCustomize() {
        return rule -> {
            // dept
            rule.addDeptColumn(NotificationCenterDO.class);
            rule.addDeptColumn(SingleMissionDO.class);
        };
    }
}
