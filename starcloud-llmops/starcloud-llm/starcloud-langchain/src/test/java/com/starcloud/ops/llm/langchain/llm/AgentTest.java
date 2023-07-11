package com.starcloud.ops.llm.langchain.llm;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseSingleActionAgent;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.tools.LoadTools;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class AgentTest extends SpringBootTests {

    @MockBean
    private DataSource dataSource;


    @Test
    public void loadToolsTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(RequestsGetTool.class), chatOpenAI);

        log.info("tools: {}", JSONUtil.parse(tools).toStringPretty());

    }


    @Test
    public void initAgentTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(RequestsGetTool.class), chatOpenAI);

        BaseSingleActionAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

        AgentExecutor agentExecutor = AgentExecutor.initializeAgent(tools, chatOpenAI, baseSingleActionAgent);

        agentExecutor.run("Who is Leo DiCaprio's girlfriend? What is her current age raised to the 0.43 power?");

        log.info("tools: {}", JSONUtil.parse(tools).toStringPretty());

    }


}
