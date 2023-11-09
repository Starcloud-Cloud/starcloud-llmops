package com.starcloud.ops.business.xhs;

import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.app.enums.xhs.XhsCreativeContentTypeEnums;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
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

import java.util.Arrays;
import java.util.List;

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
    private XhsCreativeContentService xhsCreativeContentService;


    @Test
    public void executeTest() {


        List<Long> ids = Arrays.asList(286L, 288L);

        xhsCreativeContentService.execute(ids, XhsCreativeContentTypeEnums.PICTURE.getCode(), true);

    }

    @Test
    public void executeTest2() {


        List<Long> ids = Arrays.asList(286L, 288L);

        xhsCreativeContentService.execute(ids, XhsCreativeContentTypeEnums.COPY_WRITING.getCode(), true);

    }

}
