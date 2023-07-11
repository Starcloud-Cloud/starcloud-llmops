package com.starcloud.ops.llm.langchain.core.agent.base.action;

import lombok.Data;

@Data
public class AgentAction {


    private String tools;


    private String toolInput;


    private String log;


}
