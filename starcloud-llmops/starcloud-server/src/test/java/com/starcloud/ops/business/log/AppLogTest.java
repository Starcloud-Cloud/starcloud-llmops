package com.starcloud.ops.business.log;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class AppLogTest extends BaseDbUnitTest {


    @Autowired
    private LogAppConversationService logAppConversationService;

    @Autowired
    private LogAppApi logAppApi;

    @Test
    public void getAppConversationInfoPageTest() {

        LogAppConversationInfoPageReqVO pageReqVO = new LogAppConversationInfoPageReqVO();

        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(3);

        PageResult<LogAppConversationInfoPO> result = logAppConversationService.pageLogConversation(pageReqVO);

        log.info("result: {}", result);
    }


    @Test
    public void getAppMessageStatisticsListTest() {

        LogAppMessageStatisticsListReqVO reqVO = new LogAppMessageStatisticsListReqVO();

        reqVO.setAppName("app");
        //reqVO.setAppUid("appId-test66");

        List<LogAppMessageStatisticsListPO> result = logAppConversationService.listLogMessageStatistics(reqVO);

        log.info("result: {}", result);
    }


    @Test
    public void getAppMessageResultTest() {

        LogAppMessageInfoRespVO infoRespVO = logAppApi.getAppMessageResult("b8e8ffaeb0064ea0aede52ef7920626f");

        log.info("result: {}", infoRespVO);
    }

}
