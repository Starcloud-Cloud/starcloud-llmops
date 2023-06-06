package com.starcloud.ops.llm.langchain;

import com.starcloud.ops.llm.langchain.core.chain.conversation.ConversationChain;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferWindowMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationTokenBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.OpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


@Slf4j
public class ChainTest extends SpringBootTests {

    @Test
    public void ConversationChainTest() {

        OpenAI llm = new OpenAI();

        ConversationChain conversationChain = new ConversationChain(llm, new ConversationBufferMemory());
        conversationChain.setVerbose(true);


        BaseLLMResult baseLLMResult = conversationChain.predict(Arrays.asList(BaseVariable.newString("input", "Hi there!")));

        log.info("baseLLMResult1: {}", baseLLMResult);

        baseLLMResult = conversationChain.predict(Arrays.asList(BaseVariable.newString("input", "I'm doing well! Just having a conversation with an AI.")));

        log.info("baseLLMResult2: {}", baseLLMResult);

    }


    @Test
    public void ConversationBufferWindowMemoryTest() {

        ConversationBufferWindowMemory memory = new ConversationBufferWindowMemory(2);

        memory.getChatHistory().addUserMessage("111hahha12");
        memory.getChatHistory().addAiMessage("11112333");

        memory.getChatHistory().addUserMessage("222hahha12");
        memory.getChatHistory().addAiMessage("22212333");

        memory.getChatHistory().addUserMessage("333hahha12");
        memory.getChatHistory().addAiMessage("33312333");

        memory.getChatHistory().addUserMessage("444hahha12");
        memory.getChatHistory().addAiMessage("444412333");


        List<BaseVariable> baseVariables = memory.loadMemoryVariables();


        log.info("baseVariables: {}", baseVariables);
    }


    @Test
    public void ConversationSummaryMemoryTest() {
        ConversationSummaryMemory memory = new ConversationSummaryMemory();
        // memory.setReturnMessages(true);

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "hi")
        ), BaseLLMResult.success("whats up"));

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());
    }


    @Test
    public void ChainSummaryMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationChain conversationChain = new ConversationChain(llm, new ConversationSummaryMemory(new OpenAI()));
        conversationChain.setVerbose(true);

        BaseLLMResult result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Hi, what's up?")
        ));

        log.info("baseLLMResult1: {}", result);

        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Tell me more about it!")
        ));

        log.info("baseLLMResult2: {}", result);


        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Very cool -- what is the scope of the project?")
        ));

        log.info("baseLLMResult3: {}", result);
    }


    @Test
    public void ConversationTokenBufferMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationTokenBufferMemory memory = new ConversationTokenBufferMemory(llm, 20);

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "hi")
        ), BaseLLMResult.success("whats up"));

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "not much you")
        ), BaseLLMResult.success("not much"));

        log.info("baseLLMResult3: {}", memory.loadMemoryVariables());
    }


    @Test
    public void ChainTokenBufferMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationTokenBufferMemory memory = new ConversationTokenBufferMemory(new OpenAI(), 60);

        ConversationChain conversationChain = new ConversationChain(llm, memory);
        conversationChain.setVerbose(true);


        BaseLLMResult result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Hi, what's up?")
        ));

        log.info("baseLLMResult1: {}", result);

        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Just working on writing some documentation!")
        ));

        log.info("baseLLMResult2: {}", result);


        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "For LangChain! Have you heard of it?")
        ));

        log.info("baseLLMResult3: {}", result);


        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Haha nope, although a lot of people confuse it for that")
        ));

        log.info("baseLLMResult4: {}", result);

    }


    @Test
    public void ConversationSummaryBufferMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationSummaryBufferMemory memory = new ConversationSummaryBufferMemory(new ChatOpenAI(), 90);

        ConversationChain conversationChain = new ConversationChain(llm, memory);
        conversationChain.setVerbose(true);

        BaseLLMResult result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Hi, what's up?")
        ));

        log.info("baseLLMResult1: {}", result);

        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Just working on writing some documentation!")
        ));

        log.info("baseLLMResult2: {}", result);


        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "For LangChain! Have you heard of it?")
        ));

        log.info("baseLLMResult3: {}", result);


        result = conversationChain.predict(Arrays.asList(
                BaseVariable.newString("input", "Haha nope, although a lot of people confuse it for that")
        ));

        log.info("baseLLMResult4: {}", result);

    }
}
