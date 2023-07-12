package com.starcloud.ops.llm.langchain.core.agent.base.action;

import lombok.Data;

@Data
public class AgentAction<R> {

    private String tool;

    private Object toolInput;

    private String log;

    private R observation;

    public AgentAction<R> copyObservation(R observation) {

        AgentAction<R> agentAction = new AgentAction();

        agentAction.setTool(this.getTool());
        agentAction.setToolInput(this.getToolInput());
        agentAction.setLog(this.getLog());
        agentAction.setObservation(observation);

        return agentAction;
    }

}
