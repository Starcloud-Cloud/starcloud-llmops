package com.starcloud.ops.business.app.service.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.kstry.framework.core.engine.StoryEngine;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatHandler;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.AppWorkflowService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class WorkflowTest extends BaseDbUnitTest {

    @Autowired
    private StoryEngine storyEngine;


    @Autowired
    private AppWorkflowService appWorkflowService;


    final String appId = "appId-test";

    final String requestId = "appId-request-xxx-id";

    final String stepId = "title";

    @Test
    public void demoTest() {

        log.info("hahahahhaha");
    }

    @BeforeEach
    public void before() {

        AppEntity appEntity = new AppEntity();

        appEntity.setUid(appId);
        appEntity.setName("ppId-test name");
        appEntity.setModel(AppModelEnum.CHAT.name());

        WorkflowConfigEntity appConfigEntity = new WorkflowConfigEntity();

        List<WorkflowStepWrapper> appStepWrappers = new ArrayList<>();


        appStepWrappers.add(createAppStep("title"));
        appStepWrappers.add(createAppStep("content"));
        appStepWrappers.add(createAppStep("summarize"));

        appConfigEntity.setSteps(appStepWrappers);
        appEntity.setWorkflowConfig(appConfigEntity);


        Mockito.mockStatic(AppFactory.class);
        Mockito.when(AppFactory.factory(appId)).thenReturn(appEntity);


        Mockito.when(AppFactory.factory(appId, new AppReqVO())).thenReturn(appEntity);

        Mockito.when(AppFactory.factory(appId, new AppReqVO(), stepId)).thenReturn(appEntity);


    }


    private WorkflowStepWrapper createAppStep(String title) {

        WorkflowStepWrapper appStepWrapper = new WorkflowStepWrapper();

        WorkflowStepEntity appStepEntity = new WorkflowStepEntity();

        appStepEntity.setName("chatgpt api");
        appStepEntity.setType(OpenAIChatHandler.class.getSimpleName());

        appStepWrapper.setName(title);
        appStepWrapper.setField(title);
        appStepWrapper.setFlowStep(appStepEntity);

        return appStepWrapper;
    }

    @Test
    public void testRunTest() {


        AppExecuteReqVO executeReqVO = new AppExecuteReqVO();

        executeReqVO.setAppUid("xx");
        executeReqVO.setAppReqVO(new AppReqVO());
        executeReqVO.setScene(AppSceneEnum.WEB_MARKET.name());

        SseEmitter emitter = new SseEmitter(60000L);

        executeReqVO.setSseEmitter(emitter);

        AppEntity app = AppFactory.factory(executeReqVO.getAppUid(), executeReqVO.getAppReqVO());

        app.execute(executeReqVO);

    }


    @Test
    public void fireByAppTest() {


        appWorkflowService.fireByApp(appId, AppSceneEnum.WEB_MARKET, new AppReqVO());

    }

    @Test
    public void fireByAppStepTest() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        appWorkflowService.fireByApp(appId, AppSceneEnum.WEB_MARKET, new AppReqVO(), "title");
    }


    @Test
    public void fireByAppStepStreamTest() {
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        appWorkflowService.fireByApp(appId, AppSceneEnum.WEB_MARKET, new AppReqVO(), "title", mockHttpServletResponse);

    }


    @Test
    public void fireByAppStepContentTest() {

        appWorkflowService.fireByApp(appId, AppSceneEnum.WEB_MARKET, new AppReqVO(), "content");
    }


    @Test
    public void fireByAppStepRequestIdTest() {

        appWorkflowService.fireByApp(appId, AppSceneEnum.WEB_MARKET, new AppReqVO(), "title", "requestId-test");
    }

}
