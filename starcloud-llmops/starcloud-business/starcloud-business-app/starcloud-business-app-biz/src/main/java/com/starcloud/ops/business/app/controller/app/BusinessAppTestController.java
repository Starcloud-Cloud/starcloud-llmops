package com.starcloud.ops.business.app.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.dal.mysql.LlmBusinessAppMapper;
import com.starcloud.ops.business.app.dal.redis.DemoRedisDAO;
import com.starcloud.ops.business.core.config.BusinessAppProperties;
import com.starcloud.ops.business.core.config.StarcloudServerProperties;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.time.Duration;

@RestController
@RequestMapping("/llm/app")
@Validated
@Slf4j
public class BusinessAppTestController {

    @Resource
    private StarcloudServerProperties starcloudServerProperties;

    @Resource
    private BusinessAppProperties businessAppProperties;

    @Resource
    private DemoRedisDAO demoRedisDAO;


    @Resource
    LlmBusinessAppMapper llmBusinessAppMapper;

    @GetMapping("/test")
    @Operation(summary = "test")
    @PermitAll
    public CommonResult<String> test() {

        return CommonResult.success("hello test:" + starcloudServerProperties.getTest() + "||" + businessAppProperties.getTest());
    }


    @GetMapping("/test/mysql")
    @PermitAll
    public CommonResult<String> testMysql() {

        long count = llmBusinessAppMapper.selectCount();


        return CommonResult.success("hello test:" + count);
    }

    @GetMapping("/test/redis")
    @PermitAll
    public CommonResult<String> testRedis() {

        demoRedisDAO.set("xxx", "123344", Duration.ofSeconds(5));


        return CommonResult.success("hello test:" + demoRedisDAO.get("xxx"));
    }


    @GetMapping("/test/mq")
    @PermitAll
    public CommonResult<String> testMq() {

        demoRedisDAO.set("xxx", "123344", Duration.ofSeconds(5));


        return CommonResult.success("hello test:" + demoRedisDAO.get("xxx"));
    }

}
