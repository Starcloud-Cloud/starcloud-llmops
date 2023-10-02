package com.starcloud.ops.business.chat;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

@Slf4j
@ComponentScan(basePackages = "cn.iocoder.yudao.module.infra")
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class ChatTest extends BaseDbUnitTest {

    @Autowired
    private ChatService chatService;

    @MockBean
    private PermissionApi permissionApi;

    @MockBean
    private DictDataService dictDataService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private AppPublishService appPublishService;

    @MockBean
    private FileApi fileApi;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private RedisMQTemplate redisMQTemplate;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    public void before() {

        Mockito.mockStatic(WebFrameworkUtils.class);
        Mockito.when(WebFrameworkUtils.getLoginUserId()).thenReturn(1L);

        Mockito.mockStatic(AdminUserService.class);

        AdminUserDO adminUserDO = new AdminUserDO();

        adminUserDO.setId(186L);
        adminUserDO.setTenantId(2L);

        Mockito.when(adminUserService.getUser(186L)).thenReturn(adminUserDO);
    }

    @Test
    public void initChatAppTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();
        chatRequest.setAppUid("play");
        chatRequest.setScene(AppSceneEnum.WEB_ADMIN.name());

        chatRequest.setQuery("讲个关于汉堡的笑话吧。");

        chatRequest.setQuery("Who is Leo DiCaprio's girlfriend Or ex-girlfriend? What is her current age raised to the 0.43 power?");

        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");
        ChatAppEntity<ChatRequestVO, JsonData> chatAppEntity = AppFactory.factory(chatRequest);

        JsonData jsonParams = chatAppEntity.execute(chatRequest);

    }

    /**
     * 普通对话
     */
    @Test
    public void runMyChat1Test() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        chatRequest.setAppUid("2a9ffdde1f8c4f30b36a872aa8586cc1");
        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        chatRequest.setQuery("今天杭州天气怎么样？");


        chatService.chat(chatRequest);

    }


    /**
     * 普通带历史聊天
     */
    @Test
    public void runMyChatTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");


        chatRequest.setAppUid("03a48ee3b3664e808c650d541f04d494");
        chatRequest.setConversationUid("c610cd4011d248f4902e455a668cb66e");

        chatRequest.setScene(AppSceneEnum.WEB_ADMIN.name());


        chatRequest.setQuery("继续");

        chatRequest.setQuery("亚马逊新手如何开店，详细点说明，2000个字");

        chatRequest.setQuery("新手如何上架商品？详细点说明，2000个字");

        chatRequest.setQuery("新手如何退货尼？详细点说明，1000个字");

        chatRequest.setQuery("新手如何发货尼？详细点说明，1000个字");

        chatRequest.setQuery("亚马逊新手如何开店，详细点说明，1000个字");

        chatRequest.setQuery("我的名字叫 大飞");


        chatRequest.setQuery("你还记得我的名字嘛？");


        chatRequest.setQuery("亚马逊新手如何开店，详细点说明，100个字");

        chatRequest.setQuery("你还记得我的名字嘛？");


        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatService.chat(chatRequest);

    }


    /**
     * 普通带历史聊天
     */
    @Test
    public void runMyChatUrlTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");

        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatService.chat(chatRequest);


    }

    /**
     * 带工具聊天
     */
    @Test
    public void runMyChatToolTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");

        chatRequest.setScene(AppSceneEnum.WEB_ADMIN.name());

        chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatRequest.setQuery("今天杭州的天气怎么样？");


        chatService.chat(chatRequest);

    }


    /**
     * 工具调用 并 包含 工具调用历史
     */
    @Test
    public void runMyChatToolHistoryTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");
        chatRequest.setConversationUid("0275a13139424142bad11d215c9e89cf");

        chatRequest.setScene(AppSceneEnum.WEB_ADMIN.name());


        chatRequest.setQuery("今天杭州的天气怎么样？");

        chatRequest.setQuery("今天北京的天气怎么样？");

        chatRequest.setQuery("今天天津的天气怎么样？");

        chatRequest.setQuery("今天南京的天气怎么样？");

        chatService.chat(chatRequest);

    }


    /**
     * 工具调用 联网查询
     */
    @Test
    public void runMyChatWebSearchTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();
        //chatRequest.setSseEmitter(new SseEmitter());

        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");
        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());


        chatRequest.setQuery("今天杭州的天气怎么样？");

        chatRequest.setQuery("今天北京的天气怎么样？");

        chatRequest.setQuery("今天天津的天气怎么样？");

        //chatRequest.setQuery("杭州 8月29号天怎么样，跟8月28号天气比较尼？");

        // chatRequest.setQuery("查询下 杭州 2023年8月29号和8月30号的天气，并做个温度比较给我。");


        chatRequest.setQuery("查下苹果最新的有哪些消息？");

        chatRequest.setQuery("搜索下 最新黑悟空的图片");


        chatRequest.setQuery("今天天津的天气怎么样？");

        chatService.chat(chatRequest);

    }


    /**
     * 查询带文档的chat
     */
    @Test
    public void runMyDocTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        chatRequest.setAppUid("2a651f163be6492aac1b1e612f45da8d");
        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        chatRequest.setQuery("什么是电子商务？");

        chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatRequest.setQuery("今天天津的天气怎么样？");

        chatRequest.setQuery("亚马逊商城是否适合我的业务?");

        chatRequest.setQuery("今天是几号?");

        chatService.chat(chatRequest);

    }


    /**
     * 带工具聊天 + 历史
     */
    @Test
    public void runMyChatToolHistory2Test() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");
        //chatRequest.setConversationUid("5e97181f087a4c62b672deaa4fd8d090");

        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatRequest.setQuery("1+1=?");

        chatService.chat(chatRequest);

    }

    /**
     * 带工具聊天 + 历史
     */
    @Test
    public void runImageTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        //带数据集的
        //带数据集的
        chatRequest.setAppUid("f21278f7cfb9462893efd0507f3bbafc");
        //chatRequest.setConversationUid("5e97181f087a4c62b672deaa4fd8d090");

        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatRequest.setQuery("画一个直升机");

        chatService.chat(chatRequest);

    }

    /**
     * 模型切换
     */
    @Test
    public void runModelTypeTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        chatRequest.setWebSearch(true);


        //带数据集的
        //带数据集的
        chatRequest.setAppUid("b9397ce23a284a05a4602a64fab939f0");
        //chatRequest.setConversationUid("5e97181f087a4c62b672deaa4fd8d090");

        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatRequest.setQuery("今天杭州的天气怎么样");

        chatService.chat(chatRequest);

    }

    //上下文，带 工具
    @Test
    public void runModel2TypeTest() {

        ChatRequestVO chatRequest = new ChatRequestVO();

        chatRequest.setWebSearch(true);


        //带数据集的
        //带数据集的
        chatRequest.setAppUid("ff9d2961fb084d209f8cff5c27267157");
        //chatRequest.setConversationUid("f83dbbb1056145909b24bc8db046f739");

        chatRequest.setUserId(186L);

        chatRequest.setScene(AppSceneEnum.CHAT_TEST.name());

        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatRequest.setQuery("页面主要提到了哪些APP？");

        chatRequest.setQuery("分析下https://www.hangzhou2022.cn/yywh/yyjy/202309/t20230911_71519.shtml");

        chatRequest.setQuery("杭州天气怎么样子？");

        chatService.chat(chatRequest);

    }




}
