package com.starcloud.ops.api;

import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;


@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class ShareAPPTest extends BaseDbUnitTest {


    @MockBean
    private PermissionApi permissionApi;

    @MockBean
    private DictDataService dictDataService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private PermissionService permissionService;

    @Autowired
    private EndUserServiceImpl endUserService;

    // @Test
    // public void testShareWebTest() {
    //
    //
    //     AppExecuteReqVO executeReqVO = new AppExecuteReqVO();
    //
    //     //通过页面的cookie 作为endUser, 保证cookie 不过期，或很久，保证长时间都认为是一个游客
    //     executeReqVO.setEndUser("we-xxxxxxxx");
    //
    //     executeReqVO.setAppUid("xxxxxxxx");
    //
    //
    //     executeReqVO.setAppReqVO(new AppReqVO());
    //     executeReqVO.setScene(AppSceneEnum.SHARE_WEB.name());
    //
    //     SseEmitter emitter = new SseEmitter(60000L);
    //
    //     executeReqVO.setSseEmitter(emitter);
    //
    //     AppEntity app = AppFactory.factoryShareApp(executeReqVO.getAppUid());
    //
    //     app.execute(executeReqVO);
    //
    // }
    //
    //
    // @Test
    // public void testShareApiTest() {
    //
    //
    //     AppExecuteReqVO executeReqVO = new AppExecuteReqVO();
    //
    //     executeReqVO.setEndUser("we-xxxxxxxx");
    //
    //     executeReqVO.setAppUid("2196b6cce43f41679e15487d79bde823");
    //     executeReqVO.setAppReqVO(new AppReqVO());
    //     executeReqVO.setScene(AppSceneEnum.SHARE_API.name());
    //
    //     SseEmitter emitter = new SseEmitter(60000L);
    //
    //     executeReqVO.setSseEmitter(emitter);
    //
    //     AppEntity app = AppFactory.factoryApp(executeReqVO.getAppUid());
    //
    //     app.execute(executeReqVO);
    //
    // }
    //
    // @Test
    // public void testShareJSTest() {
    //
    //
    //     AppExecuteReqVO executeReqVO = new AppExecuteReqVO();
    //
    //     executeReqVO.setEndUser("we-xxxxxxxx");
    //
    //     executeReqVO.setAppUid("2196b6cce43f41679e15487d79bde823");
    //     executeReqVO.setAppReqVO(new AppReqVO());
    //     executeReqVO.setScene(AppSceneEnum.SHARE_JS.name());
    //
    //     SseEmitter emitter = new SseEmitter(60000L);
    //
    //     executeReqVO.setSseEmitter(emitter);
    //
    //     AppEntity app = AppFactory.factoryApp(executeReqVO.getAppUid());
    //
    //     app.execute(executeReqVO);
    //
    // }


}
