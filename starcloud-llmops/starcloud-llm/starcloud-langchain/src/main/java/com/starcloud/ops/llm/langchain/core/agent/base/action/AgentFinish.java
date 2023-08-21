package com.starcloud.ops.llm.langchain.core.agent.base.action;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AgentFinish extends AgentAction {

    private String errorCode;

    private String error;

    private List<BaseMessage> messagesLog;

    private Map<String, Object> returnValues = new HashMap<>();

    public static AgentFinish error(String error, String log) {

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
        this.setLog(log);
    }

    public AgentFinish(Object returnValues, String log) {
        this.setStatus(true);
        this.returnValues.put("output", returnValues);
        this.setLog(log);
    }

}
