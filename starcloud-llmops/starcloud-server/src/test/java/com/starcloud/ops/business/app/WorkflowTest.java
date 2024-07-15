package com.starcloud.ops.business.app;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import cn.kstry.framework.core.engine.StoryEngine;
import com.starcloud.ops.business.app.controller.admin.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.request.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.app.impl.AppServiceImpl;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class WorkflowTest extends BaseDbUnitTest {


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

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private DeptService deptService;

    @MockBean
    private AdminUserApi adminUserApi;

    @Autowired
    private AppServiceImpl appService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedisMQTemplate redisMQTemplate;



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

        executeReqVO.setAppUid("dd4239ef688d42d4b2515c6214e71a79");
        executeReqVO.setAppReqVO(new AppReqVO());
        executeReqVO.setUserId(186L);
        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());

        SseEmitter emitter = new SseEmitter(60000L);

        executeReqVO.setSseEmitter(emitter);

        AppEntity app = AppFactory.factoryApp(executeReqVO.getAppUid());

        app.execute(executeReqVO);

    }


    @Test
    public void update() {

        String json = "{\"uid\":\"6b37eacb785649a4810de833428b38e8\",\"name\":\"欧洲专利查询\",\"model\":\"COMPLETION\",\"spell\":\"ouzhouzhuanlichaxun\",\"spellSimple\":\"ozzlcx\",\"type\":\"COMMON\",\"source\":\"WEB\",\"sort\":null,\"category\":\"AMAZON_SMAM\",\"tags\":[\"Text\",\"System\",\"Default\"],\"scenes\":[\"WEB_ADMIN\",\"WEB_MARKET\"],\"images\":[\"https://download.hotsalecloud.com/mofaai/images/category/amazon.jpg\"],\"icon\":\"amazon\",\"workflowConfig\":{\"steps\":[{\"field\":\"GENERATE_T33\",\"name\":\"Generate T33\",\"buttonLabel\":\"Generate Text\",\"description\":\"you can ask the AI to perform various tasks for you. You can ask it to write, rewrite, or translate an article, categorize words or elements into groups, write an email, etc\",\"flowStep\":{\"name\":\"Open API\",\"type\":\"WORKFLOW\",\"handler\":\"OpenAIChatActionHandler\",\"response\":{\"success\":true,\"type\":\"ARRAY\",\"style\":\"TEXTAREA\",\"isShow\":true,\"message\":\"欧洲专利局（EPO）：https://www.epo.org/searching-for-patents.html\\n帮我查询EP3444952B1在欧洲专利商标局的专利信息。\\n并把欧洲专利局（EPO）提供的全部专利信息提取出来，注明信息来源网址。\\n不需要提供查找步骤，只提供查询结果，输出结果为中文。\\n如果查询不到，只需要回答“欧洲专利局中没有相关专利信息，建议使用适当的专利数据库或咨询专业的专利律师”，不需要回答其他信息和信息来源网站。\",\"answer\":\"\",\"messageTokens\":0,\"messageUnitPrice\":0.000002,\"answerTokens\":0,\"answerUnitPrice\":0.000002,\"totalTokens\":419,\"totalPrice\":0.000838},\"description\":\"Open API Chat\",\"isAuto\":true,\"isCanEditStep\":true,\"version\":1,\"tags\":[\"OpenAI\",\"Completions\"],\"scenes\":[\"WEB_ADMIN\",\"WEB_MARKET\"],\"variable\":{\"variables\":[{\"label\":\"MaxTokens\",\"field\":\"max_tokens\",\"type\":\"TEXT\",\"style\":\"INPUT\",\"group\":\"MODEL\",\"order\":2,\"defaultValue\":1000,\"value\":1000,\"isShow\":false,\"isPoint\":true,\"description\":\"The total length of input tokens and generated tokens is limited by the model's context length.\"},{\"label\":\"Temperature\",\"field\":\"temperature\",\"type\":\"TEXT\",\"style\":\"INPUT\",\"group\":\"MODEL\",\"order\":1,\"defaultValue\":0.7,\"value\":0.7,\"isShow\":false,\"isPoint\":true,\"description\":\"What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic.\"},{\"label\":\"N\",\"field\":\"n\",\"type\":\"TEXT\",\"style\":\"SELECT\",\"group\":\"MODEL\",\"order\":2,\"defaultValue\":1,\"isShow\":false,\"isPoint\":true,\"description\":\"How many chat completion choices to generate for each input message. https://platform.openai.com/docs/api-reference/chat\",\"options\":[{\"label\":\"1\",\"value\":1},{\"label\":\"2\",\"value\":2},{\"label\":\"3\",\"value\":3},{\"label\":\"4\",\"value\":4},{\"label\":\"5\",\"value\":5},{\"label\":\"6\",\"value\":6},{\"label\":\"7\",\"value\":7},{\"label\":\"8\",\"value\":8},{\"label\":\"9\",\"value\":9},{\"label\":\"10\",\"value\":10}]},{\"label\":\"prompt\",\"field\":\"prompt\",\"type\":\"TEXT\",\"style\":\"TEXTAREA\",\"group\":\"MODEL\",\"order\":4,\"defaultValue\":\"Hi.\",\"value\":\"{STEP.GENERATE_T33.产品名称专利号}欧洲专利局（EPO）：https://www.epo.org/searching-for-patents.html\\n帮我查询{STEP.GENERATE_TEXT.产品名称专利号}在欧洲专利商标局的专利信息。\\n并把欧洲专利局（EPO）提供的全部专利信息提取出来，注明信息来源网址。\\n不需要提供查找步骤，只提供查询结果，输出结果为中文。\\n如果查询不到，只需要回答“欧洲专利局中没有相关专利信息，建议使用适当的专利数据库或咨询专业的专利律师”，不需要回答其他信息和信息来源网站。\",\"isShow\":false,\"isPoint\":true,\"description\":\"A text description of the desired image(s). The maximum length is 1000 characters.\"},{\"label\":\"Stream\",\"field\":\"stream\",\"type\":\"TEXT\",\"style\":\"SELECT\",\"group\":\"MODEL\",\"order\":5,\"defaultValue\":false,\"isShow\":false,\"isPoint\":true,\"description\":\"If set, partial message deltas will be sent, like in ChatGPT.\",\"options\":[{\"label\":\"false\",\"value\":false},{\"label\":\"true\",\"value\":true}]}]},\"icon\":\"open-ai\"},\"variable\":{\"variables\":[{\"label\":\"产品名称/专利号\",\"field\":\"产品名称专利号\",\"type\":\"TEXT\",\"style\":\"INPUT\",\"group\":\"PARAMS\",\"order\":0,\"value\":\"EP3444952B1\",\"isShow\":true,\"isPoint\":false,\"description\":\"\"}]}},{\"field\":\"342_12\",\"name\":\"342 12\",\"buttonLabel\":\"生成文本\",\"description\":\"你可以要求人工智能为你执行各种任务。你可以让它写、重写或翻译一篇文章，将单词或元素分类，写一封电子邮件等等。\",\"flowStep\":{\"name\":\"Open AI\",\"type\":\"WORKFLOW\",\"handler\":\"OpenAIChatActionHandler\",\"response\":{\"type\":\"TEXT\",\"style\":\"TEXTAREA\",\"isShow\":true},\"description\":\"使用Open AI API生成文本。\",\"isAuto\":true,\"isCanEditStep\":true,\"version\":1,\"tags\":[\"Open AI\",\"Completion\",\"Chat\"],\"scenes\":[\"WEB_ADMIN\",\"WEB_MARKET\"],\"variable\":{\"variables\":[{\"label\":\"最大返回Tokens\",\"field\":\"max_tokens\",\"type\":\"TEXT\",\"style\":\"INPUT\",\"group\":\"MODEL\",\"order\":2,\"defaultValue\":1000,\"value\":1000,\"isShow\":false,\"isPoint\":true,\"description\":\"在聊天完成中生成的令牌的最大数量。输入令牌和生成令牌的总长度受模型上下文长度的限制。\"},{\"label\":\"温度值\",\"field\":\"temperature\",\"type\":\"TEXT\",\"style\":\"INPUT\",\"group\":\"MODEL\",\"order\":3,\"defaultValue\":0.7,\"value\":0.7,\"isShow\":false,\"isPoint\":true,\"description\":\"采样温度在0到2之间。较高的值(如0.8)将使输出更加随机，而较低的值(如0.2)将使输出更加集中和确定。\"},{\"label\":\"Prompt\",\"field\":\"prompt\",\"type\":\"TEXT\",\"style\":\"TEXTAREA\",\"group\":\"MODEL\",\"order\":4,\"defaultValue\":\"Hi.\",\"value\":\"Hi. 1+1=?\",\"isShow\":true,\"isPoint\":true,\"description\":\"生成内容的提示符。最大长度为1000个字符。\"}]},\"icon\":\"open-ai\"}}]},\"chatConfig\":null,\"imageConfig\":null,\"actionIcons\":[\"open-ai\"],\"description\":\"欧洲专利查询\",\"publishUid\":\"5a94560059434c16a94106f51aa48ec6-1\",\"installUid\":null,\"creator\":\"2\",\"creatorName\":\"starcloudadmin\",\"updater\":\"186\",\"updaterName\":\"大飞\",\"createTime\":1691136098000,\"updateTime\":1704454426000,\"lastPublish\":null,\"tenantId\":2}";
        AppUpdateReqVO appUpdateReqVO = JsonUtils.parseObject(json, AppUpdateReqVO.class);

        appService.modify(appUpdateReqVO);

    }

}
