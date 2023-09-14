package com.starcloud.ops.llm.langchain.llm;

import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingStdOutCallbackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.chain.conversation.ConversationChain;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferWindowMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationTokenBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatQwen;
import com.starcloud.ops.llm.langchain.core.model.llm.OpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.qwen.Qwen;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ChatQwenAITest extends SpringBootTests {


    @MockBean
    private DataSource dataSource;


    @Test
    public void generateTest() {

        Qwen llm = new Qwen();

        //log.info("result : {}", llm.call("你叫什么名字"));
        //log.info("result : {}", llm.call("就当前的海洋污染的情况，写一份限塑的倡议书提纲，需要有理有据地号召大家克制地使用塑料制品"));

        log.info("result : {}", llm.call("介绍下杭州亚运会的 电竞比赛内容，说下杭州亚运会的反面新闻"));

    }


    @Test
    public void chatTest() {

        ChatQwen chatQwen = new ChatQwen();
        chatQwen.setVerbose(true);

        chatQwen.call(Arrays.asList(new HumanMessage("hi, what you name?")));

    }


    @Test
    public void chatStreamTest() {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        ChatQwen chatQwen = new ChatQwen();
        chatQwen.setVerbose(true);
        chatQwen.setStream(true);

        chatQwen.addCallbackHandler(new StreamingStdOutCallbackHandler(mockHttpServletResponse));

        chatQwen.call(Arrays.asList(new HumanMessage("hi, what you name?")));

    }


    @Test
    public void ChatPromptTemplateTest() {

        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(

                SystemMessagePromptTemplate.fromTemplate("You are a helpful assistant that translates {input_language} to {output_language}.", "input_language", "output_language"),


                HumanMessagePromptTemplate.fromTemplate("{text}", "text")

        ));


        ChatQwen chatQwen = new ChatQwen();

        LLMChain<BaseLLMResult> llmChain = new LLMChain(chatQwen, chatPromptTemplate);

        llmChain.setVerbose(true);

        String msg = llmChain.call(new HashMap() {{
            put("input_language", "English");
            put("output_language", "French");
            put("text", "I love programming.");
        }}).getText();

        log.info("chatQwen: {}", msg);

    }

}
