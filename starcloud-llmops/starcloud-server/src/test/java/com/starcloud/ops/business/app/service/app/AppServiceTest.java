package com.starcloud.ops.business.app.service.app;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbAndRedisUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class AppServiceTest extends BaseDbAndRedisUnitTest {


    @Resource
    private AppService appService;

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Test
    public void publishTest() {

        String uid = "21aee438746e4ce5bab891d3352e7fe5";

        AppRespVO appDTO = appService.getByUid(uid);
        log.info("appDTO:{}", JSON.toJSONString(appDTO));

        AppPublishReqVO request = new AppPublishReqVO();
        request.setUid(appDTO.getUid());
        request.setLanguage("zh_CN");
        request.setName(appDTO.getName());
        request.setModel(appDTO.getModel());
        request.setType(appDTO.getType());
        request.setSource(appDTO.getSource());
        request.setTags(appDTO.getTags());
        request.setCategories(appDTO.getCategories());
        request.setScenes(appDTO.getScenes());
        request.setImages(appDTO.getImages());
        request.setIcon(appDTO.getIcon());
//        request.setConfig(appDTO.getConfig());
        request.setDescription(appDTO.getDescription());

//        appService.publicAppToMarket(request);
//
//        AppDTO appDTO1 = appService.getByUid(uid);
//
//        appMapper.update(null, Wrappers.lambdaUpdate(AppDO.class)
//                .set(AppDO::getUpdater, "1")
//                .set(AppDO::getUpdateTime, LocalDateTime.now())
//                .eq(AppDO::getUid, appDTO1.getUid())
//        );
//
//        // 因为是测试环境，所以需要进行更新创建者，更新者，租户ID
//        appMarketMapper.update(null, Wrappers.lambdaUpdate(AppMarketDO.class)
//                .set(AppMarketDO::getCreator, "1")
//                .set(AppMarketDO::getUpdater, "1")
//                .set(TenantBaseDO::getTenantId, 1L)
//                .eq(AppMarketDO::getUid, AppUtils.getUid(appDTO1.getUploadUid()))
//                .eq(AppMarketDO::getVersion, AppUtils.getVersion(appDTO1.getUploadUid()))
//        );


        log.info("Publish app success");

    }

    @Test
    public void categoryTest() {
        List<AppCategoryVO> categories = appService.categories();
        log.info("categories:{}", JSON.toJSONString(categories));
    }


}
