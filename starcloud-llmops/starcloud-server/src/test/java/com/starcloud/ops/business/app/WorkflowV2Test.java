package com.starcloud.ops.business.app;

import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import cn.kstry.framework.core.engine.StoryEngine;
import com.google.common.collect.Sets;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.AppWorkflowService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.misc.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;


/**
 * 多step功能执行测试
 */
@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class WorkflowV2Test extends BaseDbUnitTest {


    @MockBean
    private PermissionApi permissionApi;

    @MockBean
    private DictDataService dictDataService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private PermissionService permissionService;


    @Autowired
    private StoryEngine storyEngine;


    @Autowired
    private AppWorkflowService appWorkflowService;

    @MockBean
    private AdminUserService adminUserService;


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


    private WorkflowStepWrapper createAppStep(String title) {

        WorkflowStepWrapper appStepWrapper = new WorkflowStepWrapper();

        WorkflowStepEntity appStepEntity = new WorkflowStepEntity();

        appStepEntity.setName("chatgpt api");
        appStepEntity.setType("OpenAIChatActionHandler");
        appStepEntity.setHandler("OpenAIChatActionHandler");

        appStepWrapper.setName(title);
        appStepWrapper.setField(title);
        appStepWrapper.setFlowStep(appStepEntity);

        return appStepWrapper;
    }

    @Test
    public void testRunTest() {


        AppExecuteReqVO executeReqVO = new AppExecuteReqVO();

        executeReqVO.setAppUid("77a466b2e70f48f9b61910105f5db0f7");
        executeReqVO.setUserId(186L);
        executeReqVO.setAppReqVO(new AppReqVO());
        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());

        SseEmitter emitter = new SseEmitter(60000L);

        //executeReqVO.setSseEmitter(emitter);

        AppEntity app = AppFactory.factoryApp(executeReqVO.getAppUid());

        app.execute(executeReqVO);

    }

    @Test
    public void spelTest() {
        // 创建spel表达式分析器
        ExpressionParser parser = new SpelExpressionParser();


        ParserContext parserContext = new ParserContext() {
            @Override
            public boolean isTemplate() {
                return true;
            }

            @Override
            public String getExpressionPrefix() {
                return "{";
            }

            @Override
            public String getExpressionSuffix() {
                return "}";
            }
        };


        HashMap<String, Object> params = new HashMap<>();


        List<HashMap> content = Arrays.asList(

                new HashMap() {{
                    put("title", "title1");
                    put("content", "content1");
                }},
                new HashMap() {{
                    put("title", "title2");
                    put("content", "content2");
                }},
                new HashMap() {{
                    put("title", "title3");
                    put("content", "content3");
                }}
        );

        StandardEvaluationContext context = new StandardEvaluationContext(new Root());
        context.setVariable("_OUT", new HashMap() {{

            put("开头", "123");
            put("段落", content);

        }});

        // 输入表达式
        Expression exp = parser.parseExpression("{STEP['开头'][key1]}", parserContext);
        // 获取表达式的输出结果，getValue入参是返回参数的类型
        String value = exp.getValue(context, String.class);
        System.out.println(value);

    }

    @Data
    public static class Root {

        private HashMap STEP = new HashMap<String, Object>() {{
            put("开头", new HashMap<String, Object>() {{
                put("key1", "vvv");
                put("key2", "XXXXX");
            }});

            put("段落", Arrays.asList(

                    new HashMap() {{
                        put("title", "title1");
                        put("content", "content1");
                    }},
                    new HashMap() {{
                        put("title", "title2");
                        put("content", "content2");
                    }},
                    new HashMap() {{
                        put("title", "title3");
                        put("content", "content3");
                    }}
            ));

        }};

    }

}
