package com.starcloud.ops.business.market;

import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-15
 */
@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class AppMarketServiceTest extends BaseDbUnitTest {

    @MockBean
    private PermissionApi permissionApi;

    @MockBean
    private DictDataService dictDataService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private PermissionService permissionService;


    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppMarketMapper appMarketMapper;


    @BeforeEach
    public void before() {


        //Mockito.mockStatic(AppFactory.class);
//        Mockito.when(AppFactory.factory(appId)).thenReturn(appEntity);
//
//
//        Mockito.when(AppFactory.factory(appId, new AppReqVO())).thenReturn(appEntity);
//
//        Mockito.when(AppFactory.factory(appId, new AppReqVO(), stepId)).thenReturn(appEntity);


        Mockito.mockStatic(SecurityFrameworkUtils.class);
        Mockito.when(SecurityFrameworkUtils.getLoginUserId()).thenReturn(1L);

    }

    @Test
    public void testRunTest() {


        AppExecuteReqVO executeReqVO = new AppExecuteReqVO();

        executeReqVO.setAppUid("8314ba99a1d9418c96862f8de66e7f7f");
        //executeReqVO.setAppReqVO(new AppReqVO());
        executeReqVO.setScene(AppSceneEnum.WEB_MARKET.name());

        SseEmitter emitter = new SseEmitter(60000L);

        executeReqVO.setSseEmitter(emitter);

        BaseAppEntity app = AppFactory.factory(executeReqVO);

        app.execute(executeReqVO);

    }

}
