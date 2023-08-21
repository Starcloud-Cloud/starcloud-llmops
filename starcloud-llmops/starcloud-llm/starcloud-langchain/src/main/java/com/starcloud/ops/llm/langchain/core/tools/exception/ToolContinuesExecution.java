package com.starcloud.ops.llm.langchain.core.tools.exception;


import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 工具发送的异常
 * 1，封装返回给llm 的内容，让llm继续下面的对话
 */
@Slf4j
@Data
public class ToolContinuesExecution extends RuntimeException {

    private String toolName;

    private Object toolInput;

    private BaseTool<Object, String> substituteTool;


    public ToolContinuesExecution(String toolName, Object toolInput, String message, Throwable cause, BaseTool substituteTool) {

        super(message, cause);
        this.toolName = toolName;
        this.toolInput = toolInput;
        this.substituteTool = substituteTool;
    }

    public String getObservation() {
        return this.getSubstituteTool().run(this.getToolInput());
    }
}
