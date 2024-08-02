package com.starcloud.ops;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


/**
 * 基础带用户服务的测试基类
 */
@Slf4j
@ComponentScan(basePackages = "cn.iocoder.yudao.module.system")
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class, YudaoDictAutoConfiguration.class, YudaoTenantAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class BaseUserContextTest extends BaseDbUnitTest {


    @MockBean
    private PermissionApi permissionApi;

    @MockBean
    private DictDataService dictDataService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private PermissionService permissionService;


    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private DeptService deptService;

    @Autowired
    private AdminUserApi adminUserApi;

    @MockBean
    private FileApi fileApi;

    @MockBean
    private RedissonClient redissonClient;


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
