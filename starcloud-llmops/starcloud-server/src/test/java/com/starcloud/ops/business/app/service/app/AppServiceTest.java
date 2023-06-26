package com.starcloud.ops.business.app.service.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class AppServiceTest extends BaseDbUnitTest {


    @Resource
    private AppService appService;

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @MockBean
    private DictDataService dictDataService;

    @Test
    public void publishTest() {
        publish("d59d4e6093aa44339e08e91db90e3a3b");
        publish("d99b403e8386495881a2e633099bf9c5");
        publish("e9028010932247d5bdb31e4c2a3a7ec0");
        publish("1eac67a6d27642619ef521d88981e45f");
        publish("5784f7cb004a43fca2df83d1ea2b1eed");
        publish("6baa8a391f644e87a34afc8b3d53e42c");
        publish("cca6daa351fc46d98cde49dc3875d20b");
        publish("519ea348208849d7bea46ef7fe363cbb");
        publish("cb1968fb354b46bb825be8747a273283");
        publish("717ceea94acd49d9935cab9e6422f779");
        publish("267a70ee737c4a17bd63d3dbc29c6fa0");
        publish("267a70ee737c4a17bd63d3dbc29c6fa0");
        publish("267a70ee737c4a17bd63d3dbc29c6fa0");
    }

    @Test
    public void bathPublishTest() {

        PageResp<AppRespVO> page = appService.page(new AppPageQuery());
        List<AppRespVO> list = page.getList();
        CollectionUtil.emptyIfNull(list).forEach(item -> publish(item.getUid()));
    }

    public void publish(String uid) {
        AppRespVO appDTO = appService.getByUid(uid);
        log.info("appDTO:{}", JSON.toJSONString(appDTO));

        AppPublishReqVO request = new AppPublishReqVO();
        request.setUid(appDTO.getUid());
        request.setLanguage("zh_CN");
        request.setCategories(appDTO.getCategories());

        appService.publish(request);

        AppRespVO byUid = appService.getByUid(uid);

        // 更新应用表
        appMapper.update(null, Wrappers.lambdaUpdate(AppDO.class)
                .set(AppDO::getUpdater, "1")
                .set(AppDO::getUpdateTime, LocalDateTime.now())
                .eq(AppDO::getUid, byUid.getUid())
        );

        // 更新市场表
        appMarketMapper.update(null, Wrappers.lambdaUpdate(AppMarketDO.class)
                .set(AppMarketDO::getCreator, "1")
                .set(AppMarketDO::getUpdater, "1")
                .set(AppMarketDO::getTenantId, 1L)
                .set(AppMarketDO::getCreateTime, LocalDateTime.now())
                .set(AppMarketDO::getUpdateTime, LocalDateTime.now())
                .eq(AppMarketDO::getUid, AppUtils.obtainUid(byUid.getUploadUid()))
                .eq(AppMarketDO::getVersion, AppUtils.obtainVersion(byUid.getUploadUid()))
        );
        log.info("Publish app success");
    }


}
