package com.starcloud.ops.business.xhs;

import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.redis.config.YudaoCacheAutoConfiguration;
import cn.iocoder.yudao.framework.redis.config.YudaoRedisAutoConfiguration;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
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

import javax.annotation.Resource;
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


    @Autowired
    private XhsCreativeContentService xhsCreativeContentService;


    @Test
    public void executeTest() {


        List<Long> ids = Arrays.asList(698L, 700L);

        xhsCreativeContentService.execute(ids, XhsCreativeContentTypeEnums.PICTURE.getCode(), true);

    }

}
