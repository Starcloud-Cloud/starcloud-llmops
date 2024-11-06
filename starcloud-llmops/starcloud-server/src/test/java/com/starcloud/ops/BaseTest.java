package com.starcloud.ops;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.tenant.config.YudaoTenantAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 基础带用户服务的测试基类
 */
@Slf4j
@ComponentScan(basePackages = "cn.iocoder.yudao.module.system")
//@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class, YudaoDictAutoConfiguration.class, YudaoTenantAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class BaseTest{


    @Test
    public void sdsdTest() {

        String getMaterialListJson = "[{\n" +
                "\t\"concept\": \"现金\",\n" +
                "\t\"concept_en\": \"Cash\",\n" +
                "\t\"explanation\": \"asdfghjkjhghj有了现金，就可以在价格低的时候买入更多的股票，就像我们在鱼便宜的时候多储备一些。而且，现金还能帮助我们在不确定的环境中保持稳定，就像在狂风中找到一个安全的港湾。\\n\\n---\\n# 概念理解\\n\\n现金是在经济活动中立即可以投入使用的货币资金，具有极高的流动性和通用性。\\n\\n# 通俗解释\\n\\n现金就像我们随时能拿出来用的钱，在经济世界里能让我们灵活应对各种变化，是一种重要的保障和工具。\",\n" +
                "\t\"image\": \"https://s.coze.cn/t/CtpbVvNg3WGJarQm/\",\n" +
                "\t\"note_title\": \"巴菲特为何大量囤现金\"\n" +
                "}]";

        List<Map<String, Object>> materialList = new ArrayList<>();


        TypeReference<List<Map<String, Object>>> reference = new TypeReference<List<Map<String, Object>>>() {
        };

        materialList = JSONUtil.toBean(getMaterialListJson, reference, false);

        log.info("materialList: {}", materialList);

    }


//
//    @BeforeEach
//    public void before() {
//
//        AppEntity appEntity = new AppEntity();
//
//        appEntity.setUid(appId);
//        appEntity.setName("ppId-test name");
//        appEntity.setModel(AppModelEnum.CHAT.name());
//
//        WorkflowConfigEntity appConfigEntity = new WorkflowConfigEntity();
//
//        List<WorkflowStepWrapper> appStepWrappers = new ArrayList<>();
//
//
//        appStepWrappers.add(createAppStep("title"));
//        appStepWrappers.add(createAppStep("content"));
//        appStepWrappers.add(createAppStep("summarize"));
//
//        appConfigEntity.setSteps(appStepWrappers);
//        appEntity.setWorkflowConfig(appConfigEntity);
//
//
//        //Mockito.mockStatic(AppFactory.class);
////        Mockito.when(AppFactory.factory(appId)).thenReturn(appEntity);
////
////
////        Mockito.when(AppFactory.factory(appId, new AppReqVO())).thenReturn(appEntity);
////
////        Mockito.when(AppFactory.factory(appId, new AppReqVO(), stepId)).thenReturn(appEntity);
//
//
//        Mockito.mockStatic(SecurityFrameworkUtils.class);
//        Mockito.when(SecurityFrameworkUtils.getLoginUserId()).thenReturn(1L);
//
//    }


}
