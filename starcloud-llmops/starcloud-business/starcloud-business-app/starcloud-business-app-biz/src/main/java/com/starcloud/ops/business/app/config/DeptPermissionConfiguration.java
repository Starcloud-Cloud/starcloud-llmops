package com.starcloud.ops.business.app.config;


import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.dal.databoject.plugin.PluginDefinitionDO;
import com.starcloud.ops.business.app.dal.databoject.prompt.DeptPromptDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.core.config.translate.TranslatorProperties;
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

            rule.addDeptColumn(MaterialLibraryAppBindDO.class);
            rule.addDeptColumn(MaterialLibraryDO.class);
            rule.addDeptColumn(MaterialLibrarySliceDO.class);
            rule.addDeptColumn(MaterialLibraryTableColumnDO.class);
            rule.addDeptColumn(PluginDefinitionDO.class);
            rule.addDeptColumn(SocialUserBindDO.class);
            rule.addDeptColumn(SocialUserDO.class);
            rule.addDeptColumn(DeptPromptDO.class);
        };
    }

    @Bean("greenClient")
    public com.aliyun.green20220302.Client greenClient(TranslatorProperties.AliyunTranslatorProperties aliyunTranslatorProperties) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(aliyunTranslatorProperties.getAccessKey())
                .setAccessKeySecret(aliyunTranslatorProperties.getSecretKey());
        // Endpoint 请参考 https://api.aliyun.com/product/Green
        config.endpoint = "green-cip.cn-beijing.aliyuncs.com";
        return new com.aliyun.green20220302.Client(config);
    }
}
