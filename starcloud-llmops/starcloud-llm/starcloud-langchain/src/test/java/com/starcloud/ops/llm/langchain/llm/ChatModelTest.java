package com.starcloud.ops.llm.langchain.llm;

import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.*;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.StreamingStdOutCallbackHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class ChatModelTest extends SpringBootTests {

    @MockBean
    private DataSource dataSource;


    @Test
    public void ChatOpenAICallTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setVerbose(true);


        chatOpenAI.call(Arrays.asList(HumanMessage.builder().content("hi, what you name?").build()));


    }


    @Test
    public void StreamingStdOutCallbackHandlerTest() {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setStream(true);
        chatOpenAI.setVerbose(true);
        chatOpenAI.addCallbackHandler(new StreamingStdOutCallbackHandler(mockHttpServletResponse));

        String msg = chatOpenAI.call(Arrays.asList(HumanMessage.builder().content("hi, what you name?").build()));

        log.info("msg: {}", msg);
    }


    @Test
    public void ChatOpenAIGenerateTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setVerbose(true);

        ChatResult chatResult = chatOpenAI.generate(Arrays.asList(
                Arrays.asList(SystemMessage.builder().content("You are a helpful assistant that translates English to French.").build(), HumanMessage.builder().content("I love programming.").build()),

                Arrays.asList(SystemMessage.builder().content("You are a helpful assistant that translates English to Chinese.").build(), HumanMessage.builder().content("I love artificial intelligence.").build())
        ));

        log.info("chatResult: {}", chatResult);

    }


    @Test
    public void PromptTemplatesTest() {


        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(

                SystemMessagePromptTemplate.fromTemplate(BasePromptTemplate.of("You are a helpful assistant that translates {input_language} to {output_language}.", "input_language", "output_language")),


                HumanMessagePromptTemplate.fromTemplate(BasePromptTemplate.of("{text}", "text"))

        ));

        PromptValue promptValue = chatPromptTemplate.formatPrompt(new HashMap() {{
            put("input_language", "English");
            put("output_language", "French");
            put("text", "I love programming.");
        }});

        log.info("promptValue: {}", promptValue);

    }


    @Test
    public void LLMChainTest() {


        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(

                SystemMessagePromptTemplate.fromTemplate(BasePromptTemplate.of("You are a helpful assistant that translates {input_language} to {output_language}.", "input_language", "output_language")),


                HumanMessagePromptTemplate.fromTemplate(BasePromptTemplate.of("{text}", "text"))

        ));


        ChatOpenAI chatOpenAI = new ChatOpenAI();

        LLMChain llmChain = new LLMChain(chatOpenAI, chatPromptTemplate);

        llmChain.setVerbose(true);

        String msg = llmChain.run(new HashMap() {{
            put("input_language", "English");
            put("output_language", "French");
            put("text", "I love programming.");
        }}).getText();

        log.info("LLMChain: {}", msg);

    }


}
