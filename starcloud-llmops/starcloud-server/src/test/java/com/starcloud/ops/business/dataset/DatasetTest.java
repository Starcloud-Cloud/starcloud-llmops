package com.starcloud.ops.business.dataset;

import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.CharacterDTO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.UploadCharacterReqVO;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.dataset.util.dataset.JsoupUtil;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import io.github.furstenheim.CopyDown;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class DatasetTest extends BaseDbUnitTest {


    @Autowired
    private DatasetSourceDataService datasetSourceDataService;


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


    @Test
    public void jsoupTest() {

        // 有语言识别问题
        String url = "https://sell.amazon.com/learn/inventory-management";
        Document doc = JsoupUtil.loadUrl(url, null);

        if (doc != null) {
            //
            doc.selectFirst("#main-nav-header").remove();

            doc.select("[class*=seller-services-footer-hmd]").remove();


            doc.select("script,.hidden,style,form").remove();

            log.info("html:\n {}", doc.toString());


            String md = JsoupUtil.html2Markdown(doc.toString());

            log.info("md:\n {}", md);

        }


    }

    @Test
    public void testSession() {
        UploadCharacterReqVO uploadCharacterReqVO = new UploadCharacterReqVO();

        uploadCharacterReqVO.setAppId("8df9343b57464dab8974b2704c6a18e4");

        uploadCharacterReqVO.setSessionId("dsadasdsadadas");
        UploadCharacterReqVO characterReqVO = new UploadCharacterReqVO();
        characterReqVO.setCleanSync(true);

        characterReqVO.setCharacterVOS(Collections.singletonList(new CharacterDTO().setTitle("title").setContext("content")));
        datasetSourceDataService.uploadCharactersSourceDataBySession(uploadCharacterReqVO, null);


    }


    @Test
    public void copyDownTest() {

        CopyDown converter = new CopyDown();
        String myHtml = "<h1>Some title</h1><div>Some html<p>Another paragraph</p></div>";
        String markdown = converter.convert(myHtml);

        log.info("markdown: {}", markdown);

    }


}
