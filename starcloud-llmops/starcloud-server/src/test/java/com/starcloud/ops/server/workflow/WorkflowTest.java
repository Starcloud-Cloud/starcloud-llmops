package com.starcloud.ops.server.workflow;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.kstry.framework.core.engine.StoryEngine;
import com.starcloud.ops.business.app.api.dto.TemplateDTO;
import com.starcloud.ops.business.app.domain.entity.AppConfigEntity;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppStepEntity;
import com.starcloud.ops.business.app.domain.entity.AppStepWrapper;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.textgeneration.OpenAIChatStepHandler;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import com.starcloud.ops.workflow.service.AppWorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.internal.bytebuddy.matcher.StringMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestClass;

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
        appEntity.setVersion("123");
        appEntity.setName("ppId-test name");

        AppConfigEntity appConfigEntity = new AppConfigEntity();

        List<AppStepWrapper> appStepWrappers = new ArrayList<>();


        appStepWrappers.add(createAppStep("title"));
        appStepWrappers.add(createAppStep("content"));
        appStepWrappers.add(createAppStep("summarize"));

        appConfigEntity.setSteps(appStepWrappers);

        appEntity.setConfig(appConfigEntity);

        Mockito.mockStatic(AppFactory.class);
        Mockito.when(AppFactory.factory(appId)).thenReturn(appEntity);


        Mockito.when(AppFactory.factory(appId, new TemplateDTO())).thenReturn(appEntity);

        Mockito.when(AppFactory.factory(appId, new TemplateDTO(), stepId)).thenReturn(appEntity);


    }


    private AppStepWrapper createAppStep(String title) {

        AppStepWrapper appStepWrapper = new AppStepWrapper();

        AppStepEntity appStepEntity = new AppStepEntity();

        appStepEntity.setName("chatgpt api");
        appStepEntity.setType(OpenAIChatStepHandler.class.getSimpleName());

        appStepWrapper.setName(title);
        appStepWrapper.setField(title);
        appStepWrapper.setStep(appStepEntity);

        return appStepWrapper;
    }

    @Test
    public void testRunTest() {



        appWorkflowService.fireByAppUid(appId);

    }


    @Test
    public void fireByAppTest() {


        appWorkflowService.fireByApp(appId, new TemplateDTO());

    }

    @Test
    public void fireByAppStepTest() {

        appWorkflowService.fireByApp(appId, new TemplateDTO(), "title");
    }

    @Test
    public void fireByAppStepContentTest() {

        appWorkflowService.fireByApp(appId, new TemplateDTO(), "content");
    }



    @Test
    public void fireByAppStepRequestIdTest() {

        appWorkflowService.fireByApp(appId, new TemplateDTO(), "title", "requestId-test");
    }

}
