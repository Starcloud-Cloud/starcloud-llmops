package com.starcloud.ops.business.xhs;

import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.errorcode.config.YudaoErrorCodeAutoConfiguration;
import cn.iocoder.yudao.framework.jackson.config.YudaoJacksonAutoConfiguration;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.sms.core.client.SmsClientFactory;
import cn.iocoder.yudao.framework.social.config.YudaoSocialAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.binarywang.spring.starter.wxjava.mp.properties.WxMpProperties;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.feign.CozePublicClient;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.job.biz.powerjob.PowerjobManager;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import com.xingyuv.jushauth.cache.AuthStateCache;
import com.xingyuv.justauth.AuthRequestFactory;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
//@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class, YudaoDictAutoConfiguration.class})
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class, YudaoSocialAutoConfiguration.class, YudaoDictAutoConfiguration.class, YudaoJacksonAutoConfiguration.class, YudaoErrorCodeAutoConfiguration.class})
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

    @MockBean
    private SmsClientFactory smsClientFactory;

    @MockBean
    private PowerjobManager powerjobManager;

    @MockBean
    private FeignContext feignContext;

    @MockBean
    private CozePublicClient cozePublicClient;

//    @MockBean
//    private AuthRequestFactory authRequestFactory;

    @MockBean
    private AuthStateCache authStateCache;

    @MockBean
    private WxMpService wxMpService;

    @MockBean
    private WxMpProperties wxMpProperties;

    @Autowired
    private CreativePlanService creativePlanService;




    @Test
    public void planPosterListTest() {

        List<PosterStyleDTO> result =   creativePlanService.planPosterList("02f76f8acc0845b9ac0d920c487f225b");

        log.info("result:{}", result);
    }



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
