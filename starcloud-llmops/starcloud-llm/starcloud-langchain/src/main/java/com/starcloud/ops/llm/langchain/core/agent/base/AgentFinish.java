package com.starcloud.ops.llm.langchain.core.agent.base;

import lombok.Data;

@Data
public class AgentFinish extends AgentAction {

    private Object returnValues;

    private String log;

    public AgentFinish(Object returnValues, String log) {
        this.returnValues = returnValues;
        this.log = log;
    }
}
