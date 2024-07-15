package com.starcloud.ops.business.xhs;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.errorcode.config.YudaoErrorCodeAutoConfiguration;
import cn.iocoder.yudao.framework.jackson.config.YudaoJacksonAutoConfiguration;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.networknt.schema.SpecVersion;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.model.creative.CreativeOptionDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeAppStepSchemeReqVO;
import com.starcloud.ops.business.app.feign.PosterImageClient;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.server.StarcloudServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Import({StarcloudServerConfiguration.class, AdapterRuoyiProConfiguration.class, YudaoSecurityAutoConfiguration.class, YudaoDictAutoConfiguration.class, YudaoJacksonAutoConfiguration.class, YudaoErrorCodeAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
@ComponentScan(basePackages = "cn.iocoder.yudao.module.system")
public class CreativeSchemeServiceTest extends BaseDbUnitTest {


//    @Resource
//    private JsonUtils jsonUtils;

    @MockBean
    private SmsSendApi smsSendApi;


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

    @Resource
    private CreativeContentService creativeContentService;


    @BeforeAll
    public static void tesd() {

        YudaoJacksonAutoConfiguration yudaoJacksonAutoConfiguration = new YudaoJacksonAutoConfiguration();
        yudaoJacksonAutoConfiguration.jsonUtils(Arrays.asList(new ObjectMapper()));

    }

    @Test
    public void sdsdTest() {

        List<Integer> arrays = new ArrayList<>();

        arrays.add(1);
        arrays.add(2);
        arrays.add(4);
        arrays.add(5);
        arrays.add(6);

        log.info("xxx: {}", CollectionUtil.sub(arrays, 1, arrays.size()));

    }

    @Test
    public void optionsTest() {

        CreativeAppStepSchemeReqVO stepSchemeReqVO = new CreativeAppStepSchemeReqVO();
        stepSchemeReqVO.setAppUid("ce625fa2885f4d2c874b1e0895ac25d0");
        stepSchemeReqVO.setStepCode("sd");

        List<CreativeOptionDTO> optionDTOS = creativeSchemeService.options(stepSchemeReqVO);

        log.info("optionDTOS: {}", JSONUtil.toJsonPrettyStr(optionDTOS));

    }


    //执行创作任务
    @Test
    public void executeTest() {

        CreativeContentExecuteReqVO creativeContentExecuteReqVO = new CreativeContentExecuteReqVO();
        creativeContentExecuteReqVO.setUid("d7f6b5a653924e2d8d904c5af8926545");
        creativeContentExecuteReqVO.setPlanUid("8da4913d201b412c82a842143c1aab9e");
        creativeContentExecuteReqVO.setForce(true);


        CreativeContentExecuteRespVO result = creativeContentService.execute(creativeContentExecuteReqVO);

        log.info("executeTest: {}", result);

    }


    @Test
    public void sdTest() {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

        try {
            JsonSchema jsonSchema = jsonSchemaGenerator.generateSchema(BookListCreativeMaterialDTO.class);


            String jj = JsonSchemaUtils.jsonNode2Str(jsonSchema);

            log.info("jj: {}", jj);
        } catch (Exception e) {

        }


    }


    @Test
    public void ddddTest() {

        com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory jsonSchemaFactory = new com.fasterxml.jackson.module.jsonSchema.factories.JsonSchemaFactory();

        ObjectSchema objectSchema = jsonSchemaFactory.objectSchema();
        objectSchema.set$schema(SpecVersion.VersionFlag.V202012.getId());
        objectSchema.setTitle("xxx");
        objectSchema.setDescription("dddddd");

        Map<String, com.fasterxml.jackson.module.jsonSchema.JsonSchema> properties = new HashMap<>();

        StringSchema stringSchema = new StringSchema();

        stringSchema.setTitle("xsdsd");
        stringSchema.setDescription("desc");
        stringSchema.setDefault("defss");

        properties.put("xx", stringSchema);

        objectSchema.setProperties(properties);


        log.info("json: {}", JsonSchemaUtils.jsonNode2Str(objectSchema));


    }
}
