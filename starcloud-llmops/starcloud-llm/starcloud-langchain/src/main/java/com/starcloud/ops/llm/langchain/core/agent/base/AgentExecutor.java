package com.starcloud.ops.llm.langchain.core.agent.base;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.chain.base.Chain;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForChainRun;
import com.starcloud.ops.llm.langchain.core.schema.parser.OutputParserException;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
public class AgentExecutor extends Chain<Void> {

    private BaseSingleActionAgent actionAgent;

    private List<BaseTool> tools;

    private BaseCallbackManager callbackManager;

    private List<String> tags;

    private Boolean returnIntermediateSteps = false;

    private int maxIterations = 15;

    private float maxExecutionTime;

    private String earlyStoppingMethod = "force";

    private List<Object> handleParsingErrors;

    public AgentExecutor(BaseSingleActionAgent actionAgent, List<BaseTool> tools, BaseCallbackManager callbackManager, List<String> tags) {
        this.actionAgent = actionAgent;
        this.tools = tools;
        this.callbackManager = callbackManager;
        this.tags = tags;
    }

    public static AgentExecutor initializeAgent(List<BaseTool> tools, BaseLanguageModel llm, BaseSingleActionAgent agent) {

        Assert.notNull(agent);

        return fromAgentAndTools(agent, tools, new CallbackManager());
    }

    private static AgentExecutor fromAgentAndTools(BaseSingleActionAgent actionAgent, List<BaseTool> tools, BaseCallbackManager callbackManager) {

        return new AgentExecutor(actionAgent, tools, callbackManager, new ArrayList<>());
    }

    protected static AgentExecutor loadAgent() {
        return null;
    }

    @Override
    protected BaseLLMResult<Void> _call(List<BaseVariable> variables, CallbackManagerForChainRun baseCallbackManager) {

        List<AgentAction> intermediateSteps = new ArrayList<>();

        Integer iterations = 0;
        long timeElapsed = 0L;
        TimeInterval timer = DateUtil.timer();

        Map<String, BaseTool> toolMap = Optional.ofNullable(this.getTools()).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(BaseTool::getName, Function.identity()));

        while (this._shouldContinue(iterations, timeElapsed)) {

            List<AgentAction> nextStepOutput = this._takeNextStep(toolMap, variables, intermediateSteps, baseCallbackManager);


        }


        timeElapsed = timer.interval();

        return null;

    }


    protected Boolean _shouldContinue(Integer iterations, long timeElapsed) {

        return true;
    }

    @Override
    public void save() {
        return;
    }

    public void saveAgent() {
        return;
    }


    public BaseTool lookupTool(String name) {

        return null;
    }


    private List<AgentAction> _takeNextStep(Map<String, BaseTool> toolMap, List<BaseVariable> variables, List<AgentAction> intermediateSteps, CallbackManagerForChainRun runManager) {

        try {

            List<AgentAction> agentActions = this.getActionAgent().plan(intermediateSteps, runManager.getChild(), variables);


            Optional.ofNullable(agentActions).orElse(new ArrayList<>()).stream().forEach(agentAction -> {

                runManager.onAgentAction(agentAction);

                //返回的工具 还在 工具集合中，就继续调用

                //工具执行
                agentAction.getTools();


            });


        } catch (OutputParserException e) {

            log.error("_takeNextStep is fail, {}", e.getMessage(), e);

        } catch (Exception e) {

            log.error("_takeNextStep is fail, {}", e.getMessage(), e);

        }

        return null;

    }
}
