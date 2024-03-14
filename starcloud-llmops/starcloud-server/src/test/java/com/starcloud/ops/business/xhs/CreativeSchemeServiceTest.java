package com.starcloud.ops.business.xhs;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.dict.config.YudaoDictAutoConfiguration;
import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.framework.security.config.YudaoSecurityAutoConfiguration;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.infra.api.file.FileApi;
import cn.iocoder.yudao.module.starcloud.adapter.ruoyipro.AdapterRuoyiProConfiguration;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeOptionDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeAppStepSchemeReqVO;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentTypeEnum;
import com.starcloud.ops.business.app.feign.PosterImageClient;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
