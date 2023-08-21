package com.starcloud.ops.business.chat;

import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

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


    @BeforeEach
    public void before() {

        Mockito.mockStatic(WebFrameworkUtils.class);
        Mockito.when(WebFrameworkUtils.getLoginUserId()).thenReturn(1L);
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



        //chatRequest.setQuery("帮我看下 https://www.google.com/doodles/celebrating-else-lasker-schuler，并总结里面的内容");

        chatService.chat(chatRequest);

    }


    /**
     * 网页爬取测试
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


}
