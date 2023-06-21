package com.starcloud.ops.business.app.service.app;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbAndRedisUnitTest;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.app.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.util.app.AppUtils;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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

        String uid = "app-a3f05c8759704139b9f878c6a68a97ea";

        AppRespVO appDTO = appService.getByUid(uid);
        log.info("appDTO:{}", JSON.toJSONString(appDTO));

        AppPublishReqVO request = new AppPublishReqVO();
        request.setUid(appDTO.getUid());
        request.setLanguage("zh_CN");
        request.setCategories(appDTO.getCategories());

        appService.publish(request);

        AppRespVO byUid = appService.getByUid(uid);

        appMapper.update(null, Wrappers.lambdaUpdate(AppDO.class)
                .set(AppDO::getUpdater, "1")
                .set(AppDO::getUpdateTime, LocalDateTime.now())
                .eq(AppDO::getUid, byUid.getUid())
        );

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
