package com.starcloud.ops.llm.langchain.core.agent.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONParser;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class BaseSingleActionAgent {

    private List<String> returnValues;


    public List<String> getAllowedTools() {
        return new ArrayList<>();
    }

    public abstract AgentAction plan(List<AgentAction> intermediateSteps, BaseCallbackManager callbackManager, List<BaseVariable> variables);


    public AgentFinish returnStoppedResponse() {

        return null;
    }

    public static BaseSingleActionAgent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, BaseCallbackManager callbackManager, List<BaseMessagePromptTemplate> extraPromptMessages, SystemMessage systemMessage) {
        return null;
    }

    public String agentType() {
        return this.getClass().getSimpleName();
    }

    public void save() {
        return;

    }


    protected List<BaseChatMessage> formatIntermediateSteps(List<AgentAction> intermediateSteps) {

        return null;
    }

    protected AgentAction parseAiMessage(BaseMessage baseMessage) {

        String callStr = (String) baseMessage.getAdditionalArgs().getOrDefault("function_call", "");

        if (StrUtil.isNotBlank(callStr)) {

            JSONObject toolInput = JSONUtil.parseObj(callStr);

            String functionName = "";
            String contentMsg = StrUtil.isNotBlank(baseMessage.getContent()) ? "responded: " + baseMessage.getContent() + "\\n" : "\n";
            String log = "\nInvoking: `" + functionName + "` with `" + toolInput + "`\n{content_msg}\n";

            FunctionsAgentAction functionsAgentAction = new FunctionsAgentAction(functionName, toolInput.toString(), log, Arrays.asList(baseMessage));

            return functionsAgentAction;
        } else {

            return new AgentFinish(BaseVariable.newString("output", baseMessage.getContent()), baseMessage.getContent());
        }

    }
}
