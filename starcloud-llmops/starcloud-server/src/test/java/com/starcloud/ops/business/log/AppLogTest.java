package com.starcloud.ops.business.log;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
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
    private LogAppMessageService logAppMessageService;

    @Autowired
    private LogAppApi logAppApi;

    @Test
    public void getAppConversationInfoPageTest() {

        AppLogConversationInfoPageReqVO pageReqVO = new AppLogConversationInfoPageReqVO();

        pageReqVO.setPageNo(1);
        pageReqVO.setPageSize(3);

        PageResult<LogAppConversationInfoPO> result = logAppConversationService.pageLogAppConversation(pageReqVO);

        log.info("result: {}", result);
    }


    @Test
    public void getAppMessageStatisticsListTest() {

        AppLogMessageStatisticsListReqVO reqVO = new AppLogMessageStatisticsListReqVO();

        reqVO.setAppName("app");
        //reqVO.setAppUid("appId-test66");

        List<LogAppMessageStatisticsListPO> result = logAppMessageService.listLogAppMessageStatistics(reqVO);

        log.info("result: {}", result);
    }


    @Test
    public void getAppMessageResultTest() {

        LogAppMessageInfoRespVO infoRespVO = logAppApi.getAppMessageResult("b8e8ffaeb0064ea0aede52ef7920626f");

        log.info("result: {}", infoRespVO);
    }

}
