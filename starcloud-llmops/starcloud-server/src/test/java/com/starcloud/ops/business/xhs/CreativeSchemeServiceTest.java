package com.starcloud.ops.business.xhs;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeOptionDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeAppStepSchemeReqVO;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.feign.PosterImageClient;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
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
public class CreativeSchemeServiceTest extends BaseDbUnitTest {


    @MockBean
    private RedisMQTemplate redisMQTemplate;


    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedissonClient redissonClient;


    @MockBean
    private FileApi fileApi;

    @MockBean
    private PosterImageClient posterImageClient;

    @Resource
    private CreativeSchemeService creativeSchemeService;


    @Test
    public void optionsTest() {

        CreativeAppStepSchemeReqVO stepSchemeReqVO = new CreativeAppStepSchemeReqVO();
        stepSchemeReqVO.setAppUid("89d53fccfffa4cb0954618dcf1a0d93e");
        stepSchemeReqVO.setStepCode("sd");

        List<CreativeOptionDTO> optionDTOS = creativeSchemeService.options(stepSchemeReqVO);

        log.info("optionDTOS: {}", JSONUtil.toJsonPrettyStr(optionDTOS));

    }
}
