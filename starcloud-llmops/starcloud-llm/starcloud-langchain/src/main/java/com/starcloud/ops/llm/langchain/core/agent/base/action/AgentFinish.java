package com.starcloud.ops.llm.langchain.core.agent.base.action;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AgentFinish extends AgentAction<Object> {

    private Map<String, Object> returnValues = new HashMap<>();

    private String log;

    public AgentFinish(Map<String, Object> returnValues, String log) {
        this.returnValues = returnValues;
        this.log = log;
    }

    public AgentFinish(Object returnValues, String log) {
        this.returnValues.put("output", returnValues);
        this.log = log;
    }
}
