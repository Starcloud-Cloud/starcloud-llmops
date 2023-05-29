package com.starcloud.ops.llm.workflow;

import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.OpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BasePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@Slf4j
public class Demo extends SpringBootApplicationTests {

    @Test
    public void run() {

        OpenAI openAI = new OpenAI();

        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt("hi, what your name")
                .maxTokens(100)
                .temperature(0.5)
                .n(1)
                .user("userId")
                .build();

        BaseLLMResult<CompletionResult> result = openAI.call(completionRequest);

        log.info("result: {}", result.getLlmOutput());

    }


    @Test
    public void chatOpenAITest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .temperature(0.5)
                .maxTokens(200)
                .n(1)
                .build();

        completionRequest.setMessages(Arrays.asList(
                new ChatMessage("system", "You are a helpful assistant."),
                new ChatMessage("user", "Who won the world series in 2020?")
        ));

        BaseLLMResult<ChatCompletionResult> result = chatOpenAI.call(completionRequest);

        //log.info("result: {}", result.getLlmOutput());

        log.info("result: {}", result.getText());

    }

    @Test
    public void tmp() {


        BasePromptTemplate template = new BasePromptTemplate("You are a helpful assistant that translates {input_language} to {output_language}.", Arrays.asList(
                BaseVariable.newString("input_language"),
                BaseVariable.newString("output_language")
        ));

        SystemMessagePromptTemplate systemMessagePromptTemplate = new SystemMessagePromptTemplate(template);


        List<BaseVariable> variables = Arrays.asList(
                BaseVariable.newString("input_language", "en"),
                BaseVariable.newString("output_language", "zh"),
                BaseVariable.newString("name", "df007df")
        );

        PromptValue prompt = systemMessagePromptTemplate.formatPrompt(variables);

        log.info("tmp: {}", prompt);


        BasePromptTemplate humpromptTemplate = new BasePromptTemplate("what you name {name}.", Arrays.asList(
                BaseVariable.newString("name")));

        HumanMessagePromptTemplate humanMessagePromptTemplate = new HumanMessagePromptTemplate(humpromptTemplate);

        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(systemMessagePromptTemplate, humanMessagePromptTemplate));

        PromptValue chatPromptValue = chatPromptTemplate.formatPrompt(variables);

        log.info("batchChatMessage: {}", chatPromptValue);


    }


    @Test
    public void llmTest() {


        BasePromptTemplate template = new BasePromptTemplate("You are a helpful assistant that translates {input_language} to {output_language}.", Arrays.asList(
                BaseVariable.newString("input_language"),
                BaseVariable.newString("output_language")
        ));

        SystemMessagePromptTemplate systemMessagePromptTemplate = new SystemMessagePromptTemplate(template);


        BasePromptTemplate humpromptTemplate = new BasePromptTemplate("what you name {name}.", Arrays.asList(
                BaseVariable.newString("name")));

        HumanMessagePromptTemplate humanMessagePromptTemplate = new HumanMessagePromptTemplate(humpromptTemplate);

        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(systemMessagePromptTemplate, humanMessagePromptTemplate));

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        LLMChain<ChatCompletionRequest, ChatCompletionResult> llmChain = new LLMChain<>(chatOpenAI, chatPromptTemplate);


        BaseLLMResult<ChatCompletionResult> result = llmChain.run("hahah");

        log.info("getLlmOutput: {}", result.getLlmOutput());

        log.info("getText: {}", result.getText());

        log.info("getUsage: {}", result.getUsage());

    }

}
