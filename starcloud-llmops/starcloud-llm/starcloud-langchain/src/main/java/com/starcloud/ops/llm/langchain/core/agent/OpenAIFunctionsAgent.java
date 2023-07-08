package com.starcloud.ops.llm.langchain.core.agent;

import cn.hutool.core.lang.Assert;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentFinish;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseSingleActionAgent;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.MessagesPlaceholder;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.utils.ConvertToOpenaiUtils;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class OpenAIFunctionsAgent extends BaseSingleActionAgent {

    private static final String TEMP_VARIABLE_SCRATCHPAD = "agent_scratchpad";

    private BaseLanguageModel llm;

    private List<BaseTool> tools;

    private BasePromptTemplate promptTemplate;

    public OpenAIFunctionsAgent(BaseLanguageModel llm, List<BaseTool> tools, BasePromptTemplate promptTemplate) {
        super();
        this.llm = llm;
        this.tools = tools;
        this.promptTemplate = promptTemplate;
    }

    @Override
    public AgentAction plan(List<AgentAction> intermediateSteps, BaseCallbackManager callbackManager, List<BaseVariable> variables) {

        List<BaseChatMessage> chatMessages = this.formatIntermediateSteps(intermediateSteps);

        List<BaseVariable> selectedInputs = Optional.ofNullable(variables).orElse(new ArrayList<>()).stream().filter(baseVariable -> !baseVariable.getField().equals(TEMP_VARIABLE_SCRATCHPAD)).collect(Collectors.toList());

        PromptValue promptValue = this.promptTemplate.formatPrompt(selectedInputs);

        List<BaseMessage> messages = promptValue.toMessage();

        BaseMessage predictedMessage = this.llm.predictMessages(messages, this.getFunctions(), callbackManager);

        AgentAction agentAction = this.parseAiMessage(predictedMessage);

        return agentAction;
    }

    @Override
    public List<String> getAllowedTools() {
        return Optional.ofNullable(tools).orElse(new ArrayList<>()).stream().map(BaseTool::getName).collect(Collectors.toList());
    }


    public List<Object> getFunctions() {

        return Optional.ofNullable(tools).orElse(new ArrayList<>()).stream().map(ConvertToOpenaiUtils::convert).collect(Collectors.toList());
    }


    public static BaseSingleActionAgent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, BaseCallbackManager callbackManager, List<BaseMessagePromptTemplate> extraPromptMessages, SystemMessage systemMessage) {

        Assert.isInstanceOf(ChatOpenAI.class, llm, "Only supported with ChatOpenAI models.");

        return new OpenAIFunctionsAgent(llm, tools, createPrompt(systemMessage, extraPromptMessages));
    }


    public static BasePromptTemplate createPrompt(SystemMessage systemMessage, List<BaseMessagePromptTemplate> extraPromptMessages) {

        List<BaseMessagePromptTemplate> promptTemplates = new ArrayList<>();

        promptTemplates.add(SystemMessagePromptTemplate.fromTemplate(systemMessage.getContent()));
        promptTemplates.addAll(extraPromptMessages);

        promptTemplates.add(HumanMessagePromptTemplate.fromTemplate("{input}"));
        promptTemplates.add(new MessagesPlaceholder("agent_scratchpad"));

        return ChatPromptTemplate.fromMessages(promptTemplates);
    }


    public static AgentAction parseAIMessage(BaseMessage baseMessage) {

        String functionCall = (String) baseMessage.getAdditionalArgs().getOrDefault("function_call", "{}");


        if (functionCall != null) {

            return null;
        } else {

            return new AgentFinish("rrrrr", baseMessage.getContent());
        }

    }

}
