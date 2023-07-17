package com.starcloud.ops.business.app.service.market;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.app.domain.llm.OpenAIToolFactory;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class LLmToolTest extends BaseDbUnitTest {


    @Test
    public void app2ToolTest() {

        log.info("app2ToolTest");

        OpenAIToolFactory.createAppTool("2196b6cce43f41679e15487d79bde823");

    }

}
