package com.starcloud.ops.llm.langchain.llm;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.config.SerpAPIToolConfig;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import com.starcloud.ops.llm.langchain.core.utils.JsonUtils;
import kong.unirest.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;


@ImportAutoConfiguration(classes = SerpAPIToolConfig.class)
@Slf4j
public class ToolTest extends SpringBootTests {

    @MockBean
    private DataSource dataSource;

    @Autowired
    private SerpAPIToolConfig serpAPIToolConfig;


    @Test
    public void SerpapiToolRunTest() {

        SerpAPITool tool = new SerpAPITool(serpAPIToolConfig.getApiKey());

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ("今天杭州天气怎么样？");

        request.setQ("特斯拉2023年上半年财报总结");

        request.setQ("2023年8月 女足比赛总比分多少？");

        String result = tool.run(request);

        log.info("jsonObject: {}", result);
    }


    @Test
    public void SerpapiToolTest() {

        SerpAPITool tool = new SerpAPITool(serpAPIToolConfig.getApiKey());

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ("今天杭州天气怎么样？");

        request.setQ("特斯拉2023年上半年财报总结");

        request.setQ("鲁迅的老师有哪些");

        List<SerpAPITool.SearchInfoDetail> searchInfoDetails = tool.runGetInfo(request);

        log.info("jsonObject: {}", JSONUtil.parse(searchInfoDetails).toStringPretty());
    }


}
