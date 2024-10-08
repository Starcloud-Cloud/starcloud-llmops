package com.starcloud.ops.business.xhs;

import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class, YudaoDictAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = "cn.iocoder.yudao.module.system")
public class XhsCreativeContentServiceTest extends BaseDbUnitTest {

//    @MockBean
//    private DictDataService dictDataService;

    @MockBean
    private RedisMQTemplate redisMQTemplate;


    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;

    @MockBean
    private FileApi fileApi;

    @Autowired
    private CreativeContentService xhsCreativeContentService;


    @Test
    public void executeTest() {



        CreativeContentExecuteReqVO creativeContentExecuteReqVO = new CreativeContentExecuteReqVO();
        creativeContentExecuteReqVO.setUid("402");
        creativeContentExecuteReqVO.setForce(true);

        xhsCreativeContentService.execute(creativeContentExecuteReqVO);

    }

    @Test
    public void executeTest2() {

        CreativeContentExecuteReqVO creativeContentExecuteReqVO = new CreativeContentExecuteReqVO();
        creativeContentExecuteReqVO.setUid("401");
        creativeContentExecuteReqVO.setForce(true);


        xhsCreativeContentService.execute(creativeContentExecuteReqVO);

    }

}
