package com.starcloud.ops.llm.langchain.core.agent.base.action;

import lombok.Data;

@Data
public class AgentAction<R> {

    private String tool;

    private Object toolInput;

    private String log;

    private R observation;
}
