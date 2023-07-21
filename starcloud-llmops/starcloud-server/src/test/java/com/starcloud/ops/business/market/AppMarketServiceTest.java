package com.starcloud.ops.business.market;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketAuditReqVO;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-15
 */
@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class AppMarketServiceTest extends BaseDbUnitTest {

    @Resource
    private AppMarketService appMarketService;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Test
    public void auditTest() {
        AppMarketAuditReqVO request = new AppMarketAuditReqVO();
        request.setUid("5ce0bded250f4dcc87c39484d2f10ba6");
        request.setVersion(2);
        request.setAudit(1);
        appMarketService.audit(request);
    }

}
