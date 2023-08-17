package com.starcloud.ops.llm.langchain.core.agent.base.action;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AgentFinish extends AgentAction {

    private Map<String, Object> returnValues = new HashMap<>();

    private String log;

    public static AgentFinish error(String error) {

        Map<String, Object> params = new HashMap();
        params.put("error", error);
        AgentFinish agentFinish = new AgentFinish(params, "");
        agentFinish.setStatus(false);

        return agentFinish;
    }

    public Object getOutput() {
        return this.returnValues.get("output");
    }

    public AgentFinish(Map<String, Object> returnValues, String log) {
        this.setStatus(true);
        this.returnValues = returnValues;
        this.log = log;
    }

    public AgentFinish(Object returnValues, String log) {
        this.setStatus(true);
        this.returnValues.put("output", returnValues);
        this.log = log;
    }

    public AgentFinish(Object returnValues, String log, BaseLLMUsage usage) {
        this.setStatus(true);
        this.returnValues.put("output", returnValues);
        this.log = log;
        this.setUsage(usage);
    }


}
